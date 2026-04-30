package aiss.peertube_miner.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.peertube_miner.model.Channel;
import aiss.peertube_miner.model.Comment;
import aiss.peertube_miner.model.Video;
import aiss.peertube_miner.model.external.ApiChannel;
import aiss.peertube_miner.model.external.ApiComment;
import aiss.peertube_miner.model.external.ApiVideo;
import aiss.peertube_miner.model.external.DataComment;
import aiss.peertube_miner.model.external.DataVideo;

@Service
public class ApiChannelService {

    @Autowired
    RestTemplate restTemplate;

    public Channel getChannelFromPeerTube(String channelId, int maxVideos, int maxComments) {

        // --- PASO 0: Obtener el Canal (Lo que ya teníamos) ---
        String urlCanal = "https://peertube.cpy.re/api/v1/video-channels/" + channelId;
        ApiChannel resCanal = restTemplate.getForObject(urlCanal, ApiChannel.class);
        
        if (resCanal == null) return null;
        
        Channel videominerChannel = new Channel();
        videominerChannel.setId(resCanal.getId().toString());
        videominerChannel.setName(resCanal.getDisplayName());
        videominerChannel.setDescription(resCanal.getDescription());
        videominerChannel.setCreatedTime(resCanal.getCreatedAt());

        // --- PASO A: Obtener los Vídeos ---
        String urlVideos = urlCanal + "/videos?count=" + maxVideos;
        // ApiVideo es el que se encarga de formatear los vídeos de PeerTube a la estructura de VideoMiner
        ApiVideo resVideos = restTemplate.getForObject(urlVideos, ApiVideo.class);

        if (resVideos != null && resVideos.getData() != null) {
            List<Video> listaVideosLimpia = new ArrayList<>();

            for (DataVideo ptVideo : resVideos.getData()) {
                // Convertimos el vídeo de PeerTube al de VideoMiner
                Video v = new Video();
                v.setId(String.valueOf(ptVideo.getId())); // 
                v.setName(ptVideo.getName());
                v.setDescription(ptVideo.getTruncatedDescription());
                v.setReleaseTime(ptVideo.getPublishedAt());
                
                // --- PASO B: Obtener los Comentarios para ESTE vídeo ---
                String urlComments = "https://peertube.cpy.re/api/v1/videos/" + ptVideo.getId() + "/comment-threads?count=" + maxComments;
                // ApiComment es el que pone bien el formato de comentarios
                ApiComment resComments = restTemplate.getForObject(urlComments, ApiComment.class);

                if (resComments != null && resComments.getData() != null) {
                    List<Comment> listaCommentsLimpia = new ArrayList<>();
                    
                    for (DataComment ptComment : resComments.getData()) {
                        Comment c = new Comment();
                        c.setId(ptComment.getId().toString());
                        c.setText(ptComment.getText());
                        c.setCreatedOn(ptComment.getCreatedAt());
                        listaCommentsLimpia.add(c);
                    }
                    v.setComments(listaCommentsLimpia);
                }

                listaVideosLimpia.add(v);
            }
            // Metemos la lista de vídeos terminada en el canal
            videominerChannel.setVideos(listaVideosLimpia);
        }

        return videominerChannel;
    }
}