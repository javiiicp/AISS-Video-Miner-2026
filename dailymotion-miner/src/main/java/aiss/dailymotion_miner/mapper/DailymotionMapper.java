package aiss.dailymotion_miner.mapper;

import java.util.ArrayList;

import aiss.dailymotion_miner.model.Channel;
import aiss.dailymotion_miner.model.Video;
import aiss.dailymotion_miner.model.external.DailymotionPlaylist;
import aiss.dailymotion_miner.model.external.DailymotionVideo;

public class DailymotionMapper {

    public static Channel toChannel(DailymotionPlaylist external) {
        Channel channel = new Channel();
        
        channel.setId(external.getId());
        channel.setName(external.getName());
        channel.setDescription(external.getDescription());
        
        if (external.getCreatedTime() != null) {
            channel.setCreatedTime(external.getCreatedTime().toString());
        }
        
        channel.setVideos(new ArrayList<>());
        
        return channel;
    }

    public static Video toVideo(DailymotionVideo external) {
        Video video = new Video();
        
        video.setId(external.getId());
        video.setName(external.getTitle()); 
        
        if (external.getCreatedTime() != null) {
            video.setReleaseTime(external.getCreatedTime().toString());
        }
        
        return video;
    }
}