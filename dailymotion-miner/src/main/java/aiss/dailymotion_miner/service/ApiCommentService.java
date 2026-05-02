package aiss.dailymotion_miner.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import aiss.dailymotion_miner.model.Comment;
import aiss.dailymotion_miner.model.external.DailymotionComment;
import aiss.dailymotion_miner.model.external.DailymotionVideoSearch;

import java.util.ArrayList;

@Service
public class ApiCommentService {

    @Autowired
    private RestTemplate restTemplate;

    public List<Comment> getCommentsAsTags(String videoId, int maxResults) {
    // 1. Pedimos tags y la fecha de creación del vídeo para usarla como referencia
    String url = "https://api.dailymotion.com/video/" + videoId + "?fields=tags,created_time";
    DailymotionComment response = restTemplate.getForObject(url, DailymotionComment.class);

    List<Comment> comments = new ArrayList<>();

    if (response != null && response.getTags() != null) {
        List<String> tags = response.getTags();
        Integer videoDate = response.getCreatedTime(); // Fecha del vídeo original

        int limit = Math.min(tags.size(), maxResults);

        for (int i = 0; i < limit; i++) {
            Comment comment = new Comment();
            
            // CAMPO ID: Consistente con VideoMiner (prefijo dm-)
            comment.setId(videoId + "-" + i);
            
            // CAMPO TEXT: El tag en sí
            comment.setText(tags.get(i)); 
            
            // CAMPO CREATEDON: Usamos la fecha del vídeo (para que no sea null)
            comment.setCreatedOn(videoDate);

            comments.add(comment);
        }
    }
    return comments;
}
    
}
