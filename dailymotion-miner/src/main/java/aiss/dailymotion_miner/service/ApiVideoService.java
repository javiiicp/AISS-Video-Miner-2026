package aiss.dailymotion_miner.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.dailymotion_miner.mapper.DailymotionMapper;
import aiss.dailymotion_miner.model.Caption;
import aiss.dailymotion_miner.model.User;
import aiss.dailymotion_miner.model.Video;
import aiss.dailymotion_miner.model.external.DailymotionVideoSearch;

@Service
public class ApiVideoService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApiUserService userService;

    public List<Video> getVideos(String playlistId) {
        return getVideos(playlistId, 10);
    }

public List<Video> getVideos(String playlistId, int maxVideos) {
    String url = "https://api.dailymotion.com/playlist/" + playlistId 
               + "/videos?fields=id,title,description,created_time,owner,subtitles";

    DailymotionVideoSearch response = restTemplate.getForObject(url, DailymotionVideoSearch.class);

    if (response != null && response.getList() != null) {
        return response.getList().stream()
        .limit(maxVideos)
        .map(externalVideo -> {
            String ownerId = externalVideo.getOwner(); 
            User user = userService.getUser(ownerId);
            Video video = DailymotionMapper.toVideo(externalVideo, user);
            return video;
        })
        .toList();
    }
    return new ArrayList<>();
}


private List<Caption> mapSubtitles(List<Object> subtitles) {
    List<Caption> captions = new ArrayList<>();
    
    if (subtitles != null) {
        for (Object subtitle : subtitles) {
            try {
                if (subtitle instanceof Map) {
                    Map<String, Object> subtitleMap = (Map<String, Object>) subtitle;
                    
                    Caption caption = new Caption();
                    caption.setId((String) subtitleMap.getOrDefault("id", ""));
                    caption.setLanguage((String) subtitleMap.getOrDefault("language", ""));
                    caption.setLink((String) subtitleMap.getOrDefault("link", ""));
                    
                    captions.add(caption);
                }
            } catch (Exception e) {
                System.out.println("Error mapeando subtitle: " + e.getMessage());
            }
        }
    }
    
    return captions;
}
}