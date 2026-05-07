package aiss.peertube_miner.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import aiss.peertube_miner.model.Comment;
import aiss.peertube_miner.mapper.PeertubeMapper;
import aiss.peertube_miner.model.external.ApiComment;
import aiss.peertube_miner.model.external.DataComment;

@Service
public class ApiCommentService {

    @Autowired
    RestTemplate restTemplate;

    public List<Comment> getComments(String videoId, Integer maxComments) {
        List<Comment> comments = new ArrayList<>();
        String urlComments = "https://peertube.tv/api/v1/videos/" + videoId + "/comment-threads?count=" + maxComments;
                
        ApiComment resComments = restTemplate.getForObject(urlComments, ApiComment.class);

        if (resComments != null && resComments.getData() != null) {
                    
            for (DataComment ptComment : resComments.getData()) {
                Comment comment = PeertubeMapper.toComment(ptComment);
                comments.add(comment);
                }
        }
        return comments;
    }
}
