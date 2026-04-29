package aiss.dailymotion_miner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.dailymotion_miner.mapper.DailymotionMapper;
import aiss.dailymotion_miner.model.Channel;
import aiss.dailymotion_miner.model.external.DailymotionPlaylist;

@Service
public class ApiChannelService {

    @Autowired
    private RestTemplate restTemplate;

    public Channel getChannel(String playlistId) {
        
        String url = "https://api.dailymotion.com/playlist/" + playlistId 
                   + "?fields=id,name,description,created_time";

        DailymotionPlaylist externalPlaylist = restTemplate.getForObject(url, DailymotionPlaylist.class);

        Channel channel = null;
        if (externalPlaylist != null) {
            channel = DailymotionMapper.toChannel(externalPlaylist);
        }

        return channel;
    }
}