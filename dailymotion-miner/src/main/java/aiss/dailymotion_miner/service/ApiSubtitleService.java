package aiss.dailymotion_miner.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import aiss.dailymotion_miner.mapper.DailymotionMapper;
import aiss.dailymotion_miner.model.Caption;
import aiss.dailymotion_miner.model.external.DailymotionSubtitle;
import aiss.dailymotion_miner.model.external.DailymotionSubtitleSearch;

@Service
public class ApiSubtitleService {

    @Autowired
    private RestTemplate restTemplate;

    public List<Caption> getSubtitles(String videoId) {
        List<Caption> captions = new ArrayList<>();
        String urlSubtitles = "https://api.dailymotion.com/video/" + videoId + "/subtitles";
        try {
            DailymotionSubtitleSearch res = restTemplate.getForObject(urlSubtitles, DailymotionSubtitleSearch.class);
            if (res != null && res.getSubtitles() != null) {
                for (DailymotionSubtitle extSub : res.getSubtitles()) {
                    captions.add(DailymotionMapper.toCaption(extSub));
                }
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo subtítulos: " + e.getMessage());
        }
        return captions;
    }
}