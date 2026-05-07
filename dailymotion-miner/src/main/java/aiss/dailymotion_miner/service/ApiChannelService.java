package aiss.dailymotion_miner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import aiss.dailymotion_miner.exception.ChannelNotFoundException;
import aiss.dailymotion_miner.mapper.DailymotionMapper;
import aiss.dailymotion_miner.model.*;
import aiss.dailymotion_miner.model.external.*;

@Service
public class ApiChannelService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ApiVideoUserService userService;
    @Autowired
    private ApiCommentService commentService;
    @Autowired
    private ApiSubtitleService subtitleService;

    public Channel getChannelFromDailymotion(String playlistId, int maxVideos, int maxPages) {
        String urlPlaylist = "https://api.dailymotion.com/playlist/" + playlistId + "?fields=id,name,description,created_time";
        DailymotionPlaylist extPlaylist;

        try {
            extPlaylist = restTemplate.getForObject(urlPlaylist, DailymotionPlaylist.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ChannelNotFoundException();
        }

        if (extPlaylist == null) throw new ChannelNotFoundException();

        Channel channel = DailymotionMapper.toChannel(extPlaylist);

        // Obtener vídeos de la playlist
        String urlVideos = "https://api.dailymotion.com/playlist/" + playlistId + "/videos?limit=" + maxVideos + "&fields=id,title,description,created_time,owner,tags";
        DailymotionVideoSearch videoSearch = restTemplate.getForObject(urlVideos, DailymotionVideoSearch.class);

        if (videoSearch != null && videoSearch.getList() != null) {
            for (DailymotionVideo extVideo : videoSearch.getList()) {
                Video video = DailymotionMapper.toVideo(extVideo);
                
                // Delegación en servicios especializados
                video.setAuthor(userService.getAuthor(extVideo.getOwner()));
                video.setComments(commentService.getComments(extVideo.getTags(), video.getId()));
                video.setCaptions(subtitleService.getSubtitles(video.getId()));

                channel.getVideos().add(video);
            }
        }
        return channel;
    }
}