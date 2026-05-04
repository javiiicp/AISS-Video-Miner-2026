package aiss.peertube_miner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aiss.peertube_miner.model.Channel;
import aiss.peertube_miner.service.ApiChannelService;
import aiss.peertube_miner.service.VideoMinerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/channels")
@Tag(name = "PeerTube Channels", description = "Extraccion y transformacion de canales PeerTube hacia VideoMiner")
@Validated
public class ChannelController {

    @Autowired
    private ApiChannelService channelService;

    @Autowired
    private VideoMinerService videoMinerService; 

    // GET http://localhost:8081/api/channels/{id}?maxVideos=10&maxComments=2
    @Operation(summary = "Obtener canal desde PeerTube", description = "Requiere id de canal y permite limitar maxVideos/maxComments. Incluye extras de paginacion, filtrado por nombre de video y ordenacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Canal obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "Canal no encontrado en PeerTube")
    })
    @GetMapping("/{id}")
    public Channel getChannel(
            @PathVariable String id, 
            @RequestParam(defaultValue = "10") @Min(1) Integer maxVideos,
            @RequestParam(defaultValue = "2") @Min(1) Integer maxComments,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return channelService.getChannelFromPeerTube(id, maxVideos, maxComments, name, page, size, sortBy, sortDir);
    }

    // POST http://localhost:8081/api/channels/{id}?maxVideos=10&maxComments=2
    @Operation(summary = "Guardar canal en VideoMiner", description = "Extrae canal de PeerTube y lo persiste en VideoMiner con los parametros indicados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Canal enviado a VideoMiner"),
            @ApiResponse(responseCode = "404", description = "Canal no encontrado en PeerTube")
    })
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Channel postChannel(@PathVariable String id,
            @RequestParam(defaultValue = "10") @Min(1) Integer maxVideos,
            @RequestParam(defaultValue = "2") @Min(1) Integer maxComments,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Channel channel = channelService.getChannelFromPeerTube(id, maxVideos, maxComments, name, page, size, sortBy, sortDir);
        
        if (channel != null) {
            videoMinerService.saveChannel(channel);
        }
        
        return channel;
    }

}