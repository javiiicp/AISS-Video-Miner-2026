package aiss.peertube_miner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.peertube_miner.model.Channel;

@Service
public class VideoMinerService {

    @Autowired
    RestTemplate restTemplate;

    // La URL de tu otro microservicio
    private final String uri = "http://localhost:8080/videominer/api/channels";

    //POST:
    public Channel saveChannel(Channel channel) {
        // Enviamos el objeto y recibimos el objeto creado (igual que tu createObject)
        return restTemplate.postForObject(uri, channel, Channel.class);
    }
}