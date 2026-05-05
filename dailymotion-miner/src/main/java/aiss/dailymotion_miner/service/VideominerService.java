package aiss.dailymotion_miner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import aiss.dailymotion_miner.model.Channel;

@Service
public class VideominerService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${VIDEOMINER_BASE_URL:http://localhost:8080}")
    private String videominerBaseUrl;
    
    public void saveChannel(Channel channel) {
        String url = videominerBaseUrl + "/videominer/channels";
        restTemplate.postForObject(url, channel, Channel.class);
    }
}