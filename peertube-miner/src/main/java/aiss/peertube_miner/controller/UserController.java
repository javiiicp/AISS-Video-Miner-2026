package aiss.peertube_miner.controller;

import aiss.peertube_miner.model.User;
import aiss.peertube_miner.model.external.ApiAccount;
import aiss.peertube_miner.service.ApiUserService;
import aiss.peertube_miner.service.VideoMinerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import aiss.peertube_miner.exception.UserNotFoundException;

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
    @ResponseStatus(HttpStatus.CREATED) // Opcional: devuelve 201 si todo va bien
    public void mineUser(@PathVariable String instance, @PathVariable String username) {
        try {
            // 1. Intentamos buscar en PeerTube
            String url = "https://" + instance + "/api/v1/accounts/" + username;
            ApiAccount externalAccount = restTemplate.getForObject(url, ApiAccount.class);

            // 2. Traducimos y enviamos (Tu parte de User)
            User user = apiUserService.transformToUser(externalAccount);
            videoMinerService.saveUser(user);

        } catch (HttpClientErrorException.NotFound e) {
            // Si PeerTube dice 404, lanzamos nuestra excepción de 404
            throw new UserNotFoundException();
        } catch (RestClientException e) {
            // Para cualquier otro error, que siga saliendo el 500
            throw e;
        }
    }
}