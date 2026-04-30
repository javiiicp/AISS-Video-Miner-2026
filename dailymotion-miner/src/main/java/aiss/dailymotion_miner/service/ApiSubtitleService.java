package aiss.dailymotion_miner.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.dailymotion_miner.model.Video;

@Service
public class ApiSubtitleService {

    @Autowired
    private RestTemplate restTemplate;

    public List<Video> getVideos(String playlistId) {
        //TODO
        return null;
    }
}