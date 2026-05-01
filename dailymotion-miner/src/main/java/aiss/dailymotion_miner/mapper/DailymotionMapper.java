package aiss.dailymotion_miner.mapper;

import java.util.ArrayList;

import aiss.dailymotion_miner.model.Channel;
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
}