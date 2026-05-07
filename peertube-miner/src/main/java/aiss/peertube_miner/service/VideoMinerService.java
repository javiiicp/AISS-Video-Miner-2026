package aiss.peertube_miner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.peertube_miner.model.Channel;

@Service
public class VideoMinerService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${VIDEOMINER_BASE_URL:http://localhost:8080}")
    private String videominerBaseUrl;
    
    public void saveChannel(Channel channel) {
        // Enviar el canal transformado a la API central de VideoMiner
        String url = videominerBaseUrl + "/videominer/channels";
        restTemplate.postForObject(url, channel, Channel.class);
    }
}