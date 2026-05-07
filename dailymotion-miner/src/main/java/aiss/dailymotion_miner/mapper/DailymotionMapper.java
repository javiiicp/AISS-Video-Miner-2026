package aiss.dailymotion_miner.mapper;

import java.time.Instant;
import java.util.ArrayList;

import aiss.dailymotion_miner.model.Caption;
import aiss.dailymotion_miner.model.Channel;
import aiss.dailymotion_miner.model.Comment;
import aiss.dailymotion_miner.model.User;
import aiss.dailymotion_miner.model.Video;
import aiss.dailymotion_miner.model.external.DailymotionPlaylist;
import aiss.dailymotion_miner.model.external.DailymotionSubtitle;
import aiss.dailymotion_miner.model.external.DailymotionUser;
import aiss.dailymotion_miner.model.external.DailymotionVideo;

public class DailymotionMapper {

    /**
     * Transforma una playlist de Dailymotion en nuestro modelo de Canal.
     */
    public static Channel toChannel(DailymotionPlaylist external) {
        Channel channel = new Channel();
        channel.setId(external.getId());
        channel.setName(external.getName());
        channel.setDescription(external.getDescription());
        
        // Conversión de Unix Timestamp (Long) a ISO String
        if (external.getCreatedTime() != null) {
            channel.setCreatedTime(Instant.ofEpochSecond(external.getCreatedTime()).toString()); 
        }
        
        channel.setVideos(new ArrayList<>());
        return channel;
    }

    /**
     * Transforma un vídeo de Dailymotion en nuestro modelo unificado.
     */
    public static Video toVideo(DailymotionVideo external) {
        Video video = new Video();
        video.setId(external.getId());
        video.setName(external.getTitle());
        video.setDescription(external.getDescription());
        
        // Conversión de Unix Timestamp (Integer) a ISO String
        if (external.getCreatedTime() != null) {
            video.setReleaseTime(Instant.ofEpochSecond(external.getCreatedTime().longValue()).toString());
        }
        
        video.setComments(new ArrayList<>());
        video.setCaptions(new ArrayList<>());
        return video;
    }

    /**
     * Mapea el usuario externo al modelo común.
     */
    public static User toUser(DailymotionUser external) {
        User user = new User();
        user.setId(external.getId());
        user.setName(external.getUsername());
        user.setUser_link(external.getUrl());
        user.setPicture_link(external.getAvatar120Url());
        return user;
    }

    /**
     * Traduce los subtítulos externos.
     */
    public static Caption toCaption(DailymotionSubtitle external) {
        Caption caption = new Caption();
        caption.setId(external.getId());
        caption.setLanguage(external.getLanguage());
        caption.setLink(external.getLink());
        return caption;
    }

    /**
     * Convierte un tag o metadato en un objeto Comment.
     */
    public static Comment toComment(Object tag, String videoId, int index) {
        Comment comment = new Comment();
        comment.setId(videoId + "-" + index);
        comment.setText(tag != null ? tag.toString() : "");
        comment.setCreatedOn(Instant.now().toString());
        return comment;
    }
}