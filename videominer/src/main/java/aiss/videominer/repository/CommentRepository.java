package aiss.videominer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aiss.videominer.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    Page<Comment> findByText(String text, Pageable pageable);
    Page<Comment> findByVideo_Id(String videoId, Pageable pageable);
    Page<Comment> findByTextContainingIgnoreCase(String text, Pageable paging);
}
