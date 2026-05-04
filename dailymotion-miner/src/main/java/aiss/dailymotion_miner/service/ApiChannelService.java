package aiss.dailymotion_miner.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import aiss.dailymotion_miner.exception.ChannelNotFoundException;
import aiss.dailymotion_miner.model.Caption;
import aiss.dailymotion_miner.model.Channel;
import aiss.dailymotion_miner.model.Comment;
import aiss.dailymotion_miner.model.User;
import aiss.dailymotion_miner.model.Video;
import aiss.dailymotion_miner.model.external.DailymotionPlaylist;
import aiss.dailymotion_miner.model.external.DailymotionSubtitle;
import aiss.dailymotion_miner.model.external.DailymotionSubtitleSearch;
import aiss.dailymotion_miner.model.external.DailymotionUser;
import aiss.dailymotion_miner.model.external.DailymotionVideo;
import aiss.dailymotion_miner.model.external.DailymotionVideoSearch;

@Service
public class ApiChannelService {

    @Autowired
    private RestTemplate restTemplate;

    public Channel getChannelFromDailymotion(String playlistId, int maxVideos, int maxPages) {
        // --- 1. OBTENER LA PLAYLIST (CANAL) ---
        String urlPlaylist = "https://api.dailymotion.com/playlist/" + playlistId;
        DailymotionPlaylist extPlaylist;
        
        try {
            extPlaylist = restTemplate.getForObject(urlPlaylist, DailymotionPlaylist.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ChannelNotFoundException();
        }

        if (extPlaylist == null) {
            return null;
        }

        // Mapeo inline a nuestro Channel del modelo común
        Channel channel = new Channel();
        channel.setId(extPlaylist.getId());
        channel.setName(extPlaylist.getName());
        channel.setDescription(extPlaylist.getDescription());
        if (extPlaylist.getCreatedTime() != null) {
            channel.setCreatedTime(extPlaylist.getCreatedTime().toString());
        }
        channel.setVideos(new ArrayList<>());

        // --- 2. OBTENER VÍDEOS DE LA PLAYLIST ---
        String urlVideos = "https://api.dailymotion.com/playlist/" + playlistId + "/videos?limit=" + maxVideos;
        try {
            DailymotionVideoSearch videoSearch = restTemplate.getForObject(urlVideos, DailymotionVideoSearch.class);

            if (videoSearch != null && videoSearch.getList() != null) {
                for (DailymotionVideo extVideo : videoSearch.getList()) {
                    
                    // Mapeo inline a nuestro Video del modelo común
                    Video video = new Video();
                    video.setId(extVideo.getId());
                    video.setName(extVideo.getTitle());
                    video.setDescription(extVideo.getDescription());
                    if (extVideo.getCreatedTime() != null) {
                        video.setReleaseTime(extVideo.getCreatedTime().toString());
                    }

                    // --- 3. OBTENER EL USUARIO (OWNER) ---
                    User author = new User();
                    author.setId(extVideo.getOwner());
                    author.setName("User " + extVideo.getOwner()); // fallback en caso de error
                    
                    try {
                        String urlUser = "https://api.dailymotion.com/user/" + extVideo.getOwner();
                        DailymotionUser extUser = restTemplate.getForObject(urlUser, DailymotionUser.class);
                        if (extUser != null) {
                            author.setName(extUser.getUsername());
                            author.setUser_link(extUser.getUrl());
                            author.setPicture_link(extUser.getAvatar120Url());
                        }
                    } catch (RestClientException e) {
                        System.out.println("Error obteniendo usuario del vídeo: " + e.getMessage());
                    }
                    video.setAuthor(author);

                    // --- 4. MAPEO DE TAGS COMO COMENTARIOS ---
                    List<Comment> listComments = new ArrayList<>();
                    if (extVideo.getTags() != null) {
                        int commentIdIndex = 1;
                        for (Object tagObj : extVideo.getTags()) {
                            if (tagObj != null) {
                                Comment comment = new Comment();
                                comment.setId(extVideo.getId() + "-" + commentIdIndex);
                                comment.setText(tagObj.toString());
                                comment.setCreatedOn(Instant.now().toString());
                                listComments.add(comment);
                                commentIdIndex++;
                            }
                        }
                    }
                    video.setComments(listComments);

                    // --- 5. OBTENER Y MAPEAR CAPTIONS ---
                    List<Caption> listCaptions = new ArrayList<>();
                    String urlSubtitles = "https://api.dailymotion.com/video/" + extVideo.getId() + "/subtitles";
                    try {
                        DailymotionSubtitleSearch subSearch = restTemplate.getForObject(urlSubtitles, DailymotionSubtitleSearch.class);
                        if (subSearch != null && subSearch.getSubtitles() != null) {
                            for (DailymotionSubtitle extSub : subSearch.getSubtitles()) {
                                Caption caption = new Caption();
                                caption.setId(extSub.getId());
                                caption.setLanguage(extSub.getLanguage());
                                caption.setLink(extSub.getLink());
                                listCaptions.add(caption);
                            }
                        }
                    } catch (RestClientException e) {
                        System.out.println("Error obteniendo captions: " + e.getMessage());
                    }
                    video.setCaptions(listCaptions);

                    channel.getVideos().add(video);
                }
            }
        } catch (RestClientException e) {
            System.out.println("Error obteniendo los vídeos de Dailymotion: " + e.getMessage());
        }

        return channel;
    }
}