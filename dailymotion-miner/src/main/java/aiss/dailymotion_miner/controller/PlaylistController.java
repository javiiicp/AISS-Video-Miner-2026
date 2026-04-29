package aiss.dailymotion_miner.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aiss.dailymotion_miner.model.Channel;
import aiss.dailymotion_miner.model.Video;
import aiss.dailymotion_miner.service.ApiChannelService;
import aiss.dailymotion_miner.service.ApiVideoService;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    private ApiChannelService channelService;

    @Autowired
    private ApiVideoService videoService;

    // GET http://localhost:8080/api/playlists/{id}
    @GetMapping("/{id}")
    public Channel getPlaylist(@PathVariable String id) {
        Channel channel = channelService.getChannel(id);
        
        if (channel != null) {
            List<Video> videos = videoService.getVideos(id);
            
            channel.setVideos(videos);
        }
        
        return channel;
    }
}