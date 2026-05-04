package aiss.peertube_miner.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import aiss.peertube_miner.model.Caption;
import aiss.peertube_miner.model.Channel;
import aiss.peertube_miner.model.Comment;
import aiss.peertube_miner.model.Video;
import aiss.peertube_miner.model.external.ApiCaption;
import aiss.peertube_miner.model.external.ApiChannel;
import aiss.peertube_miner.model.external.ApiComment;
import aiss.peertube_miner.model.external.ApiVideo;
import aiss.peertube_miner.model.external.DataCaption;
import aiss.peertube_miner.model.external.DataComment;
import aiss.peertube_miner.model.external.DataVideo;

@Service
public class ApiChannelService {

    @Autowired
    RestTemplate restTemplate;

    public Channel getChannelFromPeerTube(String channelId, int maxVideos, int maxComments) {

        // --- PASO 0: Obtener el Canal (Lo que ya teníamos) ---
        String urlCanal = "https://peertube.tv/api/v1/video-channels/" + channelId;
        ApiChannel resCanal;
        try {
            resCanal = restTemplate.getForObject(urlCanal, ApiChannel.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Canal no encontrado en PeerTube", ex);
        }

        if (resCanal == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Canal no encontrado en PeerTube");
        }
        
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
                String urlComments = "https://peertube.tv/api/v1/videos/" + ptVideo.getId() + "/comment-threads?count=" + maxComments;
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

                // --- PASO C: Obtener los CAPTIONS para ESTE vídeo ---
                String urlCaptions = "https://peertube.tv/api/v1/videos/" + ptVideo.getId() + "/captions";
                try {
                    ApiCaption resCaptions = restTemplate.getForObject(urlCaptions, ApiCaption.class);
                    
                    if (resCaptions != null && resCaptions.getData() != null) {
                        List<Caption> listaCaptionsLimpia = new ArrayList<>();
                        
                        for (DataCaption ptCaption : resCaptions.getData()) {
                            Caption caption = new Caption();
                            caption.setId(ptCaption.getId());
                            caption.setLanguage(ptCaption.getLanguage());
                            caption.setName(ptCaption.getLanguage());
                            listaCaptionsLimpia.add(caption);
                        }
                        v.setCaptions(listaCaptionsLimpia);
                    }
                } catch (Exception e) {
                    System.out.println("Error obteniendo captions: " + e.getMessage());
                }  

                listaVideosLimpia.add(v);
            }
            // Metemos la lista de vídeos terminada en el canal
            videominerChannel.setVideos(listaVideosLimpia);
        }

        return videominerChannel;
    }
}