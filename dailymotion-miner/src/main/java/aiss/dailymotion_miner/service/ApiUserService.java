package aiss.dailymotion_miner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import aiss.dailymotion_miner.mapper.DailymotionMapper;
import aiss.dailymotion_miner.model.User;
import aiss.dailymotion_miner.model.external.DailymotionUser;

public class ApiUserService {
        @Autowired
    private RestTemplate restTemplate;

    public User getUser(String userId) {
        
        String url = "https://api.dailymotion.com/user/" + userId 
                   + "?fields=id,username,url,avatar_120_url";

        DailymotionUser externalUser = restTemplate.getForObject(url, DailymotionUser.class);

        User user = null;
        if (externalUser != null) {
            user = DailymotionMapper.toUser(externalUser);
        }

        return user ;
    }
}
