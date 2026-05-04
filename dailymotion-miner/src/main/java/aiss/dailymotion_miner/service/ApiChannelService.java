package aiss.dailymotion_miner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import aiss.dailymotion_miner.mapper.DailymotionMapper;
import aiss.dailymotion_miner.model.Channel;
import aiss.dailymotion_miner.model.external.DailymotionPlaylist;

@Service
public class ApiChannelService {

    @Autowired
    private RestTemplate restTemplate;

    public Channel getChannel(String playlistId) {
        //Consulta
        //GET https://api.dailymotion.com/playlist/{playlistId}?fields=id,name,description,created_time
        String url = "https://api.dailymotion.com/playlist/" + playlistId 
                   + "?fields=id,name,description,created_time";
        //Validar si la playlist existe, si no existe lanzar una excepción con código 404
        DailymotionPlaylist externalPlaylist;
        try {
            externalPlaylist = restTemplate.getForObject(url, DailymotionPlaylist.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist no encontrada en Dailymotion", ex);
        }
        //Validar si la respuesta es nula, si es nula lanzar una excepción con código 404
        if (externalPlaylist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist no encontrada en Dailymotion");
        }
        // Mapeo de DailymotionPlaylist a Channel
        return DailymotionMapper.toChannel(externalPlaylist);
    }
}