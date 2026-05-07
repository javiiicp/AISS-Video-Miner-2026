package aiss.peertube_miner.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.peertube_miner.mapper.PeertubeMapper;
import aiss.peertube_miner.model.Caption;
import aiss.peertube_miner.model.external.ApiCaption;
import aiss.peertube_miner.model.external.DataCaption;

@Service
public class ApiCaptionService {

    @Autowired
    private RestTemplate restTemplate;

    public List<Caption> getCaption(String videoId) {
        List<Caption> captions = new ArrayList<>();       
        String urlCaptions = "https://peertube.tv/api/v1/videos/" + videoId + "/captions";
        
        try {
            ApiCaption resCaptions = restTemplate.getForObject(urlCaptions, ApiCaption.class);
            if (resCaptions != null && resCaptions.getData() != null) {
                for (DataCaption ptCaption : resCaptions.getData()) {
                    captions.add(PeertubeMapper.toCaption(ptCaption));
                }
            }
        } catch (Exception e) {
            // Loguear error real en producción
            System.err.println("Error al obtener captions para videoId " + videoId + ": " + e.getMessage());
        }  
        return captions;
    }
}