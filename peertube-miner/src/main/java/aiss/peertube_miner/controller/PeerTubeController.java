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
import aiss.peertube_miner.service.PeerTubeService;
import aiss.peertube_miner.service.VideoMinerService;

@RestController
@RequestMapping("/peertube") // O la ruta que prefieras
public class PeerTubeController {

    @Autowired
    PeerTubeService peerTubeService;

    @Autowired
    VideoMinerService videoMinerService;

    // OPERACIÓN GET 
    @GetMapping("/{instance}/{id}")
    public Channel getPreview(
            @PathVariable String instance,
            @PathVariable String id,
            @RequestParam(defaultValue = "10") int maxVideos,
            @RequestParam(defaultValue = "2") int maxComments) {
        
        // Solo busca y devuelve el JSON para que lo veas en el navegador
        return peerTubeService.getChannel(instance, id, maxVideos, maxComments);
    }

    // OPERACIÓN POST
    @PostMapping("/{instance}/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createMining(
            @PathVariable String instance,
            @PathVariable String id,
            @RequestParam(defaultValue = "10") int maxVideos,
            @RequestParam(defaultValue = "2") int maxComments) {
        
        // 1. Extrae los datos
        Channel canal = peerTubeService.getChannel(instance, id, maxVideos, maxComments);
        
        // 2. Si existe, lo envía a VideoMiner
        if (canal != null) {
            videoMinerService.saveChannel(canal);
        }
    }
}