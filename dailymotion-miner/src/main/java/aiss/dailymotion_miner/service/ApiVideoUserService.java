package aiss.dailymotion_miner.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.dailymotion_miner.mapper.DailymotionMapper;
import aiss.dailymotion_miner.model.User;
import aiss.dailymotion_miner.model.Video;
import aiss.dailymotion_miner.model.external.DailymotionUser;
import aiss.dailymotion_miner.model.external.DailymotionVideo;
import aiss.dailymotion_miner.model.external.DailymotionVideoSearch;

@Service
public class ApiVideoUserService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApiCommentService commentService;

    @Autowired
    private ApiSubtitleService subtitleService;

    public List<Video> getVideoUser(String playlistId, int maxVideos) {
        List<Video> videos = new ArrayList<>();
        String urlVideos = "https://api.dailymotion.com/playlist/" + playlistId + "/videos?limit=" + maxVideos + "&fields=id,title,description,created_time,owner,tags";
        DailymotionVideoSearch videoSearch = restTemplate.getForObject(urlVideos, DailymotionVideoSearch.class);

        if (videoSearch != null && videoSearch.getList() != null) {
            for (DailymotionVideo extVideo : videoSearch.getList()) {
                Video video = DailymotionMapper.toVideo(extVideo);
                
                // Delegación en servicios especializados
                video.setAuthor(getAuthor(extVideo.getOwner()));
                video.setComments(commentService.getComments(extVideo.getTags(), video.getId()));
                video.setCaptions(subtitleService.getSubtitles(video.getId()));

                videos.add(video);
            }
        }
        return videos;
    }

    public User getAuthor(String ownerId) {
        try {
            String urlUser = "https://api.dailymotion.com/user/" + ownerId + "?fields=id,username,url,avatar_120_url";
            DailymotionUser extUser = restTemplate.getForObject(urlUser, DailymotionUser.class);
            if (extUser != null) {
                return DailymotionMapper.toUser(extUser);
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo autor: " + e.getMessage());
        }
        // Fallback en caso de error
        User fallback = new User();
        fallback.setId(ownerId);
        fallback.setName("User " + ownerId);
        return fallback;
    }
}