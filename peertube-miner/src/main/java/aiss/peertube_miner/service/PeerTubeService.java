package aiss.peertube_miner.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.peertube_miner.model.Channel;
import aiss.peertube_miner.model.Video;
import aiss.peertube_miner.model.VideoSearchResponse;

@Service
public class PeerTubeService {

    @Autowired
    RestTemplate restTemplate;

    public Channel getChannel(String instance, String channelId, int maxVideos, int maxComments) {
        String baseUrl = "https://" + instance + "/api/v1";

        // 1. Obtener el Canal
        String urlCanal = baseUrl + "/video-channels/" + channelId;
        Channel canal = restTemplate.getForObject(urlCanal, Channel.class);

        if (canal != null) {
            // 2. Obtener los Vídeos (usando maxVideos)
            String urlVideos = urlCanal + "/videos?count=" + maxVideos;
            VideoSearchResponse resVideos = restTemplate.getForObject(urlVideos, VideoSearchResponse.class);

            if (resVideos != null && resVideos.getData() != null) {
                List<Video> videos = resVideos.getData();

                // 3. PARA CADA VÍDEO -> Buscar sus comentarios
                for (Video video : videos) {
                    // URL de hilos de comentarios del vídeo (usando maxComments)
                    String urlComments = baseUrl + "/videos/" + video.getId() + "/comment-threads?count=" + maxComments;
                    
                    CommentResponse resComments = restTemplate.getForObject(urlComments, CommentResponse.class);
                    
                    if (resComments != null) {
                        video.setComments(resComments.getData());
                    }
                }
                // Guardamos la lista de vídeos ya "completados" en el canal
                canal.setVideos(videos);
            }
        }
        return canal;
    }
}