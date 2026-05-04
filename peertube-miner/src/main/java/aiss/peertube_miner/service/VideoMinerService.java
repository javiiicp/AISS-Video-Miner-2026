package aiss.peertube_miner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.peertube_miner.model.Channel;

@Service
public class VideoMinerService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${VIDEOMINER_BASE_URL:http://localhost:8080}")
    private String videominerBaseUrl;

    private String channelUrl() { return videominerBaseUrl + "/videominer/channels"; }

    /**
     * Envía un canal ya mapeado al servicio central VideoMiner para guardarlo.
     */
    public void saveChannel(Channel channel) {
        // Usamos postForObject para enviar el objeto por la red
        restTemplate.postForObject(channelUrl(), channel, Channel.class);
    }
}