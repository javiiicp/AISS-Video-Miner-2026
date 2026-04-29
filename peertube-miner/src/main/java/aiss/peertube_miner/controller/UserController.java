package aiss.peertube_miner.controller;

import aiss.peertube_miner.model.User;
import aiss.peertube_miner.model.external.ApiAccount;
import aiss.peertube_miner.service.ApiUserService;
import aiss.peertube_miner.service.VideoMinerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/peertubeminer/users")
public class UserController {

    @Autowired
    ApiUserService apiUserService; // Tu traductor

    @Autowired
    VideoMinerService videoMinerService; // Tu mensajero

    @Autowired
    RestTemplate restTemplate;

    @PostMapping("/{instance}/{username}")
    public void mineUser(@PathVariable String instance, @PathVariable String username) {
        // 1. Buscamos en PeerTube
        String url = "https://" + instance + "/api/v1/accounts/" + username;
        ApiAccount externalAccount = restTemplate.getForObject(url, ApiAccount.class);

        // 2. Traducimos a vuestro modelo User usando TU servicio
        User user = apiUserService.transformToUser(externalAccount);

        // 3. Enviamos a VideoMiner para que lo guarde
        if (user != null) {
            videoMinerService.saveUser(user);
        }
    }
}