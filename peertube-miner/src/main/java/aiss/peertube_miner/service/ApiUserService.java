package aiss.peertube_miner.service;

import org.springframework.stereotype.Service;

import aiss.peertube_miner.model.User;
import aiss.peertube_miner.model.external.ApiAccount;

@Service
public class ApiUserService {

    /**
     * Transforma una cuenta de la API de PeerTube al modelo interno de VideoMiner.
     */
    public User transformToUser(ApiAccount apiAccount) {
        User user = new User();

        // 1. Mapear el ID (PeerTube da un Integer, tú necesitas un String)
        if (apiAccount.getId() != null) {
            user.setId(apiAccount.getId().toString());
        }

        // 2. Mapear el Nombre (En PeerTube el nombre real es displayName)
        user.setName(apiAccount.getDisplayName());

        // 3. Mapear el Link (En PeerTube es url)
        user.setUser_link(apiAccount.getUrl());

        // 4. Mapear la Imagen (Buscamos en la lista de avatars)
        if (apiAccount.getAvatars() != null && !apiAccount.getAvatars().isEmpty()) {
            // Cogemos la URL del primer avatar disponible
            String imageUrl = apiAccount.getAvatars().get(0).getFileUrl();
            user.setPicture_link(imageUrl);
        }

        return user;
    }
}