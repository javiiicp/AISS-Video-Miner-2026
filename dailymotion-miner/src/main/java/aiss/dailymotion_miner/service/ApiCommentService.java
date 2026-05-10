package aiss.dailymotion_miner.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import aiss.dailymotion_miner.mapper.DailymotionMapper;
import aiss.dailymotion_miner.model.Comment;

@Service
public class ApiCommentService {

    public List<Comment> getComments(List<Object> tags, String videoId, Integer maxComments) {
        List<Comment> comments = new ArrayList<>();
        if (tags != null) {
            int index = 1;
            for (Object tag : tags) {
                if (maxComments != null && index > maxComments) {
                    break;
                }
                comments.add(DailymotionMapper.toComment(tag, videoId, index++));
            }
        }
        return comments;
    }
}