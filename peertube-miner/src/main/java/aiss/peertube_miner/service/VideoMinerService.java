package aiss.peertube_miner.service;

import aiss.peertube_miner.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.peertube_miner.model.Channel;

@Service
public class VideoMinerService {

    @Autowired
    RestTemplate restTemplate;

    // Esta es la URL de tu microservicio central (VideoMiner)
    // El puerto suele ser 8080 y la ruta la definiste en el UserController de VideoMiner
    private final String videoMinerUrl = "http://localhost:8080/videominer/users";
    private final String channelUrl = "http://localhost:8080/videominer/channels";



    /**
     * Envía un usuario ya mapeado al servicio central VideoMiner para guardarlo.
     */
    public void saveUser(User user) {
        // Usamos postForObject para enviar el objeto por la red
        restTemplate.postForObject(videoMinerUrl, user, User.class);
    }

    /**
     * Envía un canal ya mapeado al servicio central VideoMiner para guardarlo.
     */
    public void saveChannel(Channel channel) {
        // Usamos postForObject para enviar el objeto por la red
        restTemplate.postForObject(channelUrl, channel, Channel.class);
    }
}