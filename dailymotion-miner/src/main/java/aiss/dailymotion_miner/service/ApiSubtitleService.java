package aiss.dailymotion_miner.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.dailymotion_miner.model.Caption;
import aiss.dailymotion_miner.model.Video;

@Service
public class ApiSubtitleService {

    @Autowired
    private RestTemplate restTemplate;


    public List<Caption> getSubtitlesByVideoId(String videoId) {
        String url = "https://api.dailymotion.com/video/" + videoId + "/subtitles";
        
        try {
            Object response = restTemplate.getForObject(url, Object.class);
            
            if (response != null) {
                return parseSubtitles(response, videoId);
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo subtítulos: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    private List<Caption> parseSubtitles(Object response, String videoId) {
        List<Caption> captions = new ArrayList<>();
        return captions;
    }
}