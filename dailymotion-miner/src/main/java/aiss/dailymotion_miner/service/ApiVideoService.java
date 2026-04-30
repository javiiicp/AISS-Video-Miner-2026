package aiss.dailymotion_miner.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.dailymotion_miner.mapper.DailymotionMapper;
import aiss.dailymotion_miner.model.Video;
import aiss.dailymotion_miner.model.external.DailymotionVideoSearch;

@Service
public class ApiVideoService {

    @Autowired
    private RestTemplate restTemplate;

    public List<Video> getVideos(String playlistId) {
        String url = "https://api.dailymotion.com/playlist/" + playlistId 
                   + "/videos?fields=id,title,description,created_time,tags,owner";

        DailymotionVideoSearch response = restTemplate.getForObject(url, DailymotionVideoSearch.class);

        if (response != null && response.getList() != null) {
            return response.getList().stream()
                    .map(DailymotionMapper::toVideo)
                    .toList();
        }
        return new ArrayList<>();
    }
}