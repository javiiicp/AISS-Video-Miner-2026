package aiss.dailymotion_miner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service; // Importante
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import aiss.dailymotion_miner.mapper.DailymotionMapper;
import aiss.dailymotion_miner.model.User;
import aiss.dailymotion_miner.model.external.DailymotionUser;

@Service 
public class ApiUserService {

    @Autowired
    private RestTemplate restTemplate; 

    public User getUser(String userId) {
        String url = "https://api.dailymotion.com/user/" + userId 
                   + "?fields=id,username,url,avatar_120_url";

        DailymotionUser externalUser;
        try {
            externalUser = restTemplate.getForObject(url, DailymotionUser.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado en Dailymotion", ex);
        }

        if (externalUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado en Dailymotion");
        }

        return DailymotionMapper.toUser(externalUser);
    }
}