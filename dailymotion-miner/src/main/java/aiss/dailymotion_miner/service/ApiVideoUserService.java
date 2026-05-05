package aiss.dailymotion_miner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import aiss.dailymotion_miner.mapper.DailymotionMapper;
import aiss.dailymotion_miner.model.User;
import aiss.dailymotion_miner.model.external.DailymotionUser;

@Service
public class ApiVideoUserService {

    @Autowired
    private RestTemplate restTemplate;

    public User getAuthor(String ownerId) {
        try {
            String urlUser = "https://api.dailymotion.com/user/" + ownerId + "?fields=id,username,url,avatar_120_url";
            DailymotionUser extUser = restTemplate.getForObject(urlUser, DailymotionUser.class);
            if (extUser != null) {
                return DailymotionMapper.toUser(extUser);
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo autor: " + e.getMessage());
        }
        // Fallback en caso de error
        User fallback = new User();
        fallback.setId(ownerId);
        fallback.setName("User " + ownerId);
        return fallback;
    }
}