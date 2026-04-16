package aiss.videominer.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aiss.videominer.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {}
