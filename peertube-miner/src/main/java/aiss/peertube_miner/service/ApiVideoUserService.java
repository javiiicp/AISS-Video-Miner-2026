package aiss.peertube_miner.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.peertube_miner.mapper.PeertubeMapper;
import aiss.peertube_miner.model.User;
import aiss.peertube_miner.model.Video;
import aiss.peertube_miner.model.external.ApiVideo;
import aiss.peertube_miner.model.external.DataVideo;


@Service
public class ApiVideoUserService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Extrae los vídeos de un canal usando su handle.
     */
    public List<Video> getVideoUser(String handle, Integer maxVideos) {
        List<Video> videos = new ArrayList<>();
        // Endpoint oficial de PeerTube para vídeos por canal (handle)
        String urlVideos = "https://peertube.tv/api/v1/video-channels/" + handle + "/videos?count=" + maxVideos;
        
        ApiVideo resVideos = restTemplate.getForObject(urlVideos, ApiVideo.class);

        if (resVideos != null && resVideos.getData() != null) {
            for (DataVideo ptVideo : resVideos.getData()) {
                Video video = PeertubeMapper.toVideo(ptVideo);
                // Asignamos el autor extrayéndolo de los metadatos de la cuenta
                User user = PeertubeMapper.toUser(ptVideo.getAccount());
                video.setAuthor(user);
                videos.add(video);
            }
        }
        return videos;
    }
}