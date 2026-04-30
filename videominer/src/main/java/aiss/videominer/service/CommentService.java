package aiss.videominer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import aiss.videominer.exception.CommentNotFoundException;
import aiss.videominer.model.Comment;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CommentRepository;
import aiss.videominer.repository.VideoRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;

    public CommentService(CommentRepository commentRepository, VideoRepository videoRepository) {
        this.commentRepository = commentRepository;
        this.videoRepository = videoRepository;
    }

    public Page<Comment> findAll(String text, Pageable paging) {
        if (text != null && !text.isBlank()) {
            return commentRepository.findByTextContainingIgnoreCase(text, paging);
        }
        return commentRepository.findAll(paging);
    }

    public Comment findOne(String id) {
        return commentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);
    }

    public Comment create(Comment comment) {
        Video video = resolveVideo(comment);
        comment.setVideo(video);
        return commentRepository.save(comment);
    }

    public Comment update(String id, Comment updatedComment) {
        Comment comment = findOne(id);
        comment.setText(updatedComment.getText());
        comment.setCreatedOn(updatedComment.getCreatedOn());
        Video video = resolveVideo(updatedComment);
        comment.setVideo(video);
        comment.setId(id);
        return commentRepository.save(comment);
    }

    public void delete(String id) {
        if (!commentRepository.existsById(id)) {
            throw new CommentNotFoundException();
        }
        commentRepository.deleteById(id);
    }

    public Page<Comment> findByVideo(String videoId, Pageable paging) {
        videoRepository.findById(videoId)
            .orElseThrow(CommentNotFoundException::new);        
        return commentRepository.findByVideo_Id(videoId, paging);
    }

    private Video resolveVideo(Comment comment) {
        if (comment.getVideo() == null || comment.getVideo().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El comentario debe estar asociado a un vídeo existente");
        }

        return videoRepository.findById(comment.getVideo().getId())
            .orElseThrow(CommentNotFoundException::new);    
    }
}
