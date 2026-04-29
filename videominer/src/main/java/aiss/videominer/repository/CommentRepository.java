package aiss.videominer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aiss.videominer.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    
    Page<Comment> findByName(String name, Pageable pageable);
    Page<Comment> findByVideoId(String videoId, Pageable pageable);
    Page<Comment> findByNameContainingIgnoreCase(String name, Pageable paging);
}
