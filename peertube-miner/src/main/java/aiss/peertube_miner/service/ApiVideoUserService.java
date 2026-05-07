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
    RestTemplate restTemplate;

    public List<Video> getVideoUser(String channelId, Integer maxVideos) {
        List<Video> videos = new ArrayList<>();
        String urlCanal = "https://peertube.tv/api/v1/video-channels/" + channelId;
        String urlVideos = urlCanal + "/videos?count=" + maxVideos;
        // ApiVideo es el que se encarga de formatear los vídeos de PeerTube a la estructura de VideoMiner
        ApiVideo resVideos = restTemplate.getForObject(urlVideos, ApiVideo.class);

        if (resVideos != null && resVideos.getData() != null) {

            for (DataVideo ptVideo : resVideos.getData()) {
                Video video = PeertubeMapper.toVideo(ptVideo);
                User user = PeertubeMapper.toUser(ptVideo.getAccount());
                video.setAuthor(user);
                videos.add(video);
            }
        }
        return videos;
    }
    /*
    public User transformToUser(ApiAccount apiAccount) {
        User user = new User();

        // 1. Mapear el ID (PeerTube da un Integer, tú necesitas un String)
        if (apiAccount.getId() != null) {
            user.setId(apiAccount.getId().toString());
        }

        // 2. Mapear el Nombre (En PeerTube el nombre real es displayName)
        user.setName(apiAccount.getDisplayName());

        // 3. Mapear el Link (En PeerTube es url)
        user.setUser_link(apiAccount.getUrl());

        // 4. Mapear la Imagen (Buscamos en la lista de avatars)
        if (apiAccount.getAvatars() != null && !apiAccount.getAvatars().isEmpty()) {
            // Cogemos la URL del primer avatar disponible
            String imageUrl = apiAccount.getAvatars().get(0).getFileUrl();
            user.setPicture_link(imageUrl);
        }

        return user;
    }*/
}