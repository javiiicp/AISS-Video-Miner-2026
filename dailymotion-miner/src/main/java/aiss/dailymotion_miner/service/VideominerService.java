package aiss.dailymotion_miner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.dailymotion_miner.model.Channel;

@Service
public class VideominerService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${VIDEOMINER_BASE_URL:http://localhost:8080}")
    private String videominerBaseUrl;

    public Channel saveChannel(Channel channel) {
        String url = videominerBaseUrl + "/videominer/channels";
        ResponseEntity<Channel> resp = restTemplate.postForEntity(url, channel, Channel.class);
        return resp.getBody();
    }
}
