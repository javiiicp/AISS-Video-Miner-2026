package aiss.peertube_miner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.peertube_miner.model.Channel;
import aiss.peertube_miner.model.VideoSearchResponse;

@Service
public class PeerTubeService {

    @Autowired
    RestTemplate restTemplate;

    public Channel getChannel(String instance, String channelId, int maxVideos) {
        // 1. URL del canal
        String urlCanal = "https://" + instance + "/api/v1/video-channels/" + channelId;
        
        // 2. Obtener el canal (Igual que en clase)
        Channel canal = restTemplate.getForObject(urlCanal, Channel.class);

        if (canal != null) {
            // 3. URL de los vídeos
            String urlVideos = urlCanal + "/videos?count=" + maxVideos;
            
            // 4. Obtener la respuesta de vídeos y extraer la lista
            VideoSearchResponse res = restTemplate.getForObject(urlVideos, VideoSearchResponse.class);
            if (res != null) {
                canal.setVideos(res.getData());
            }
        }
        return canal;
    }
}