package aiss.peertube_miner.mapper;

import java.util.ArrayList;

import aiss.peertube_miner.model.Caption;
import aiss.peertube_miner.model.Channel;
import aiss.peertube_miner.model.Comment;
import aiss.peertube_miner.model.User;
import aiss.peertube_miner.model.Video;
import aiss.peertube_miner.model.external.ApiAccount;
import aiss.peertube_miner.model.external.ApiChannel;
import aiss.peertube_miner.model.external.DataCaption;
import aiss.peertube_miner.model.external.DataComment;
import aiss.peertube_miner.model.external.DataVideo;

public class PeertubeMapper {
    public static Channel toChannel(ApiChannel external) {
        Channel channel = new Channel();
        
        channel.setId(external.getId().toString());
        channel.setName(external.getName());
        channel.setDescription(external.getDescription());
        channel.setCreatedTime(external.getCreatedAt() != null ? external.getCreatedAt() : null);
        channel.setVideos(new ArrayList<>());
        
        return channel;
    }
    public static Video toVideo(DataVideo external) {
        Video video = new Video();
        
        video.setId(external.getId().toString());
        video.setName(external.getName()); 
        video.setDescription(external.getTruncatedDescription());
        video.setAuthor(new User());
        video.setReleaseTime(external.getPublishedAt() != null ? external.getPublishedAt() : null);
        video.setComments(new ArrayList<>()); 
        video.setCaptions(new ArrayList<>());
        if (external.getPublishedAt() != null) {
            video.setReleaseTime(external.getPublishedAt());
        }
        
        return video;
    }

    public static User toUser(ApiAccount external) {
        User user = new User();
        
        user.setId(external.getId().toString());
        user.setName(external.getDisplayName());
        user.setUser_link(external.getUrl());
        if (external.getAvatars() != null && !external.getAvatars().isEmpty()) {
            user.setPicture_link(external.getAvatars().get(0).getFileUrl());
        }
        return user;
    }

    public static Caption toCaption(DataCaption external) {
        Caption caption = new Caption();
        caption.setId(external.getId());
        caption.setLanguage(external.getLanguage());
        caption.setName(external.getLanguage());
        return caption;
    }

    public static Comment toComment(DataComment external) {
        Comment comment = new Comment();
        comment.setId(external.getId().toString());
        comment.setText(external.getText());
        comment.setCreatedOn(external.getCreatedAt() != null ? external.getCreatedAt() : null);
        return comment;
    }
}