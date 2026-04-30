package aiss.peertube_miner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aiss.peertube_miner.model.Channel;
import aiss.peertube_miner.service.ApiChannelService;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    @Autowired
    private ApiChannelService channelService;

    // GET http://localhost:8081/api/channels/{id}?maxVideos=10&maxComments=2
    @GetMapping("/{id}")
    public Channel getChannel(
            @PathVariable String id, 
            @RequestParam(defaultValue = "10") Integer maxVideos, 
            @RequestParam(defaultValue = "2") Integer maxComments) {
        
        // Llamamos al servicio con los parámetros correctos
        return channelService.getChannelFromPeerTube(id, maxVideos, maxComments);
    }
}