package aiss.videominer.repository;

import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import aiss.videominer.model.Video;

@Repository
public class VideoRepository {
    List<Video> videos = new ArrayList<>();


    public List<Video> findAll() {
        return videos;
    }

    public Video findOneById(String id) {
        return videos.stream().filter(v->v.getId().equals(id)).findFirst().orElse(null);
    }
}
