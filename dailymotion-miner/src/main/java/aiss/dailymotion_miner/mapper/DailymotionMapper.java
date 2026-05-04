package aiss.dailymotion_miner.mapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

import aiss.dailymotion_miner.model.Caption;
import aiss.dailymotion_miner.model.Channel;
import aiss.dailymotion_miner.model.Comment;
import aiss.dailymotion_miner.model.User;
import aiss.dailymotion_miner.model.Video;
import aiss.dailymotion_miner.model.external.DailymotionPlaylist;
import aiss.dailymotion_miner.model.external.DailymotionUser;
import aiss.dailymotion_miner.model.external.DailymotionVideo;

public class DailymotionMapper {


    public static Channel toChannel(DailymotionPlaylist external) {
        Channel channel = new Channel();
        
        channel.setId(external.getId());
        channel.setName(external.getName());
        channel.setDescription(external.getDescription());
        channel.setCreatedTime(external.getCreatedTime() != null ? external.getCreatedTime().toString() : null);
        channel.setVideos(new ArrayList<>());
        
        return channel;
    }

    public static Video toVideo(DailymotionVideo external, User user) {
        Video video = new Video();
        
        video.setId(external.getId());
        video.setName(external.getTitle()); 
        video.setDescription(external.getDescription());
        video.setAuthor(user);
        video.setReleaseTime(external.getCreatedTime() != null ? external.getCreatedTime().toString() : null);
        video.setComments(external.getTags() != null ? new ArrayList<>() : null); 
        video.setCaptions(new ArrayList<>());
        if (external.getCreatedTime() != null) {
            video.setReleaseTime(external.getCreatedTime().toString());
        }
        
        return video;
    }
    

    public static User toUser(DailymotionUser external) {
        User user = new User();
        
        user.setId(external.getId());
        user.setName(external.getUsername());
        user.setUser_link(external.getUrl());
        user.setPicture_link(external.getAvatar120Url());
        
        return user;
    }
    
    public static Caption toCaption(Map<String, Object> subtitleMap) {
        Caption caption = new Caption();
        
        caption.setId((String) subtitleMap.getOrDefault("id", ""));
        caption.setLanguage((String) subtitleMap.getOrDefault("language", ""));
        caption.setLink((String) subtitleMap.getOrDefault("link", ""));
        
        return caption;
    }

    public static Comment toComment(String tag, String videoId, Integer i, Integer createdOn) {
        Comment comment = new Comment();

        comment.setId(videoId + "-" + i);
        comment.setText(tag);
        Instant instante = Instant.ofEpochSecond(createdOn);
        comment.setCreatedOn(instante.toString());

        return comment;
    }
}