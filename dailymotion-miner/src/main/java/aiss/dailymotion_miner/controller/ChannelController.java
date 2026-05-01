package aiss.dailymotion_miner.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aiss.dailymotion_miner.model.Channel;
import aiss.dailymotion_miner.model.Video;
import aiss.dailymotion_miner.service.ApiChannelService;
import aiss.dailymotion_miner.service.ApiVideoService;
import aiss.dailymotion_miner.service.VideominerService;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    @Autowired
    private ApiChannelService channelService;

    @Autowired
    private ApiVideoService videoService;

    @Autowired
    private VideominerService videominerService;

    // GET http://localhost:8080/api/channels/{id}
    @GetMapping("/{id}")
    public Channel getChannel(@PathVariable String id) {
        Channel channel = channelService.getChannel(id);
        
        if (channel != null) {
            List<Video> videos = videoService.getVideos(id);
            
            channel.setVideos(videos);
        }
        
        return channel;
    }
    
    // POST http://localhost:8080/api/channels/{id}
    @PostMapping("/{id}")
    public ResponseEntity<?> saveChannelToVideoMiner(@PathVariable String id) {
        Channel channel = channelService.getChannel(id);
        List<Video> videos = videoService.getVideos(id);
        channel.setVideos(videos);

        try {
            Channel saved = videominerService.saveChannel(channel);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving channel to videominer: " + e.getMessage());
        }
    }
    
}