package aiss.videominer.repository;
import aiss.videominer.model.Caption;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptionRepository extends JpaRepository<Caption, String> {
    List<Caption> findByVideoId(String videoId);
}
