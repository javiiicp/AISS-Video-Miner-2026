package aiss.dailymotion_miner.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.dailymotion_miner.mapper.DailymotionMapper;
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
        
        try {
            if (response instanceof List) {
                List<?> subtitleList = (List<?>) response;
                
                for (Object subtitle : subtitleList) {
                    if (subtitle instanceof Map) {
                        Map<String, Object> subtitleMap = (Map<String, Object>) subtitle;
                        Caption caption = DailymotionMapper.toCaption(subtitleMap);
                        captions.add(caption);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error parseando subtítulos: " + e.getMessage());
        }
        
        return captions;
    }
}