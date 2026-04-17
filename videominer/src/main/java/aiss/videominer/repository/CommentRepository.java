package aiss.videominer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aiss.videominer.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    List<Comment> findByVideoId(String videoId);
}
