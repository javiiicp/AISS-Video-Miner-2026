package aiss.peertube_miner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    @Autowired
    private ApiChannelService channelService;

    @Autowired
    private VideoMinerService videoMinerService; 

    // GET http://localhost:8081/api/channels/{id}?maxVideos=10&maxComments=2
    @GetMapping("/{id}")
    public Channel getChannel(
            @PathVariable String id, 
            @RequestParam(defaultValue = "10") Integer maxVideos, 
            @RequestParam(defaultValue = "2") Integer maxComments) {
        
        // Llamamos al servicio con los parámetros correctos
        return channelService.getChannelFromPeerTube(id, maxVideos, maxComments);
    }

    // POST http://localhost:8081/api/channels/{id}?maxVideos=10&maxComments=2
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED) // Indica que se ha creado un recurso
    public Channel postChannel(@PathVariable String id, 
            @RequestParam(defaultValue = "10") Integer maxVideos, 
            @RequestParam(defaultValue = "2") Integer maxComments) {
        
        // A. Extraemos y transformamos los datos
        Channel channel = channelService.getChannelFromPeerTube(id, maxVideos, maxComments);
        
        // B. Si todo ha ido bien, lo enviamos a VideoMiner
        if (channel != null) {
            videoMinerService.saveChannel(channel);
        }
        
        return channel; // Devolvemos el canal para confirmar qué se ha guardado
    }

}