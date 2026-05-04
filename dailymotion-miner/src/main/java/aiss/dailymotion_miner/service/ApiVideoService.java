package aiss.dailymotion_miner.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.dailymotion_miner.mapper.DailymotionMapper;
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
        return getVideos(playlistId, 10, 2);
    }

    public List<Video> getVideos(String playlistId, int maxVideos, int maxPages) {
        List<Video> videos = new ArrayList<>();

        for (int page = 1; page <= maxPages; page++) {
            //Consulta
            //GET https://api.dailymotion.com/playlist/{playlistId}/videos?page={page}&limit={maxVideos}&fields=id,title,description,created_time,owner
            String url = "https://api.dailymotion.com/playlist/" + playlistId 
                       + "/videos?page=" + page + "&limit=" + maxVideos
                       + "&fields=id,title,description,created_time,owner";
            DailymotionVideoSearch response = restTemplate.getForObject(url, DailymotionVideoSearch.class);
            //Validar
            if (response == null || response.getList() == null || response.getList().isEmpty()) {
                break;
            }
            // Mapear cada DailymotionVideo a Video
            for (var externalVideo : response.getList()) {
                String ownerId = externalVideo.getOwner(); 
                User user = userService.getUser(ownerId);
                Video video = DailymotionMapper.toVideo(externalVideo, user);
                videos.add(video);
            }
        }

        return videos;
    }
}