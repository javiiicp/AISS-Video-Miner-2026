package aiss.peertube_miner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aiss.peertube_miner.model.Channel;
import aiss.peertube_miner.service.ApiChannelService;

@RestController
@RequestMapping("/api/playlists")
public class ChannelController {

    @Autowired
    private ApiChannelService channelService;


    // GET http://localhost:8080/api/playlists/{id}
    @GetMapping("/{id}")
    public Channel getChannel(@PathVariable String id, @PathVariable Integer maxVideo, @PathVariable Integer maxComment) {
        Channel channel = channelService.getChannelFromPeerTube(id, maxVideo, maxComment);
        
        return channel;
    }
}