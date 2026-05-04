package aiss.dailymotion_miner.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aiss.dailymotion_miner.model.Channel;
import aiss.dailymotion_miner.model.User;
import aiss.dailymotion_miner.model.Video;
import aiss.dailymotion_miner.service.ApiChannelService;
import aiss.dailymotion_miner.service.ApiCommentService;
import aiss.dailymotion_miner.service.ApiSubtitleService;
import aiss.dailymotion_miner.service.ApiUserService;
import aiss.dailymotion_miner.service.ApiVideoService;
import aiss.dailymotion_miner.service.VideominerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/channels")
@Tag(name = "Channels (Dailymotion)", description = "Operaciones para obtener y sincronizar canales de Dailymotion")
public class ChannelController {

    @Autowired
    private ApiChannelService channelService;

    @Autowired
    private ApiVideoService videoService;

    @Autowired
    private ApiCommentService commentService;

    @Autowired
    private VideominerService videominerService;
    
    @Autowired
    private ApiSubtitleService subtitleService;
    

    // GET http://localhost:8082/api/channels/{id}?maxVideos=10&maxComments=2
    @Operation(summary = "Obtener canal de Dailymotion", description = "Obtiene un canal de Dailymotion con sus vídeos y comentarios")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Canal obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Canal no encontrado")
    })
    @GetMapping("/{id}")
    public Channel getChannel(
            @PathVariable String id,
            @RequestParam(defaultValue = "10") Integer maxVideos,
            @RequestParam(defaultValue = "2") Integer maxComments) {
        Channel channel = channelService.getChannel(id);
        
        if (channel != null) {
            List<Video> videos = videoService.getVideos(id, maxVideos, maxComments);
            for (Video video : videos) {
                video.setComments(commentService.getCommentsAsTags(video.getId(), maxComments));
                video.setCaptions(subtitleService.getSubtitlesByVideoId(video.getId()));
               
            }
            channel.setVideos(videos);
        }
        
        return channel;
    }
    
    // POST http://localhost:8082/api/channels/{id}?maxVideos=10&maxComments=2
    @Operation(summary = "Guardar canal en VideoMiner", description = "Obtiene un canal de Dailymotion y lo guarda en la base de datos central de VideoMiner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Canal guardado exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error al guardar el canal")
    })
    @PostMapping("/{id}")
    public ResponseEntity<?> saveChannelToVideoMiner(
            @PathVariable String id,
            @RequestParam(defaultValue = "10") Integer maxVideos,
            @RequestParam(defaultValue = "2") Integer maxComments) {
        Channel channel = channelService.getChannel(id);
        if (channel != null) {
            List<Video> videos = videoService.getVideos(id, maxVideos, maxComments);
            for (Video video : videos) {
                video.setComments(commentService.getCommentsAsTags(video.getId(), maxComments));
                video.setCaptions(subtitleService.getSubtitlesByVideoId(video.getId()));
               
            }
            channel.setVideos(videos);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Channel not found in Dailymotion");
        }

        try {
            Channel saved = videominerService.saveChannel(channel);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving channel to videominer: " + e.getMessage());
        }
    }
}