package aiss.videominer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import aiss.videominer.exception.CommentNotFoundException;
import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Comment;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CommentRepository;
import aiss.videominer.repository.VideoRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;

    // Inyección por constructor para una mejor arquitectura
    public CommentService(CommentRepository commentRepository, VideoRepository videoRepository) {
        this.commentRepository = commentRepository;
        this.videoRepository = videoRepository;
    }

    public Page<Comment> findAll(String text, Pageable paging) {
        // Filtro de búsqueda robusto para el texto del comentario
        if (text != null && !text.isBlank()) {
            return commentRepository.findByTextContainingIgnoreCase(text, paging);
        }
        return commentRepository.findAll(paging);
    }

    public Comment findOne(String id) {
        // Uso de la excepción personalizada de comentario
        return commentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);
    }

    public Comment create(Comment comment) {
        // Resolvemos el vídeo antes de guardar
        Video video = resolveVideo(comment);
        comment.setVideo(video);
        return commentRepository.save(comment);
    }

    public Comment update(String id, Comment updatedComment) {
        Comment comment = findOne(id);
        comment.setText(updatedComment.getText());
        comment.setCreatedOn(updatedComment.getCreatedOn());
        
        // Actualizamos la relación con el vídeo si es necesario
        Video video = resolveVideo(updatedComment);
        comment.setVideo(video);
        comment.setId(id);
        return commentRepository.save(comment);
    }

    public void delete(String id) {
        // Verificación de existencia con excepción propia[cite: 101]
        if (!commentRepository.existsById(id)) {
            throw new CommentNotFoundException();
        }
        commentRepository.deleteById(id);
    }

    public Page<Comment> findByVideo(String videoId, Pageable paging) {
        videoRepository.findById(videoId)
            .orElseThrow(VideoNotFoundException::new);        
        return commentRepository.findByVideo_Id(videoId, paging);
    }

    /**
     * Método privado para validar que el vídeo asociado existe.
     * Lanza VideoNotFoundException si el ID del vídeo no es válido.
     */
    private Video resolveVideo(Comment comment) {
        if (comment.getVideo() == null || comment.getVideo().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El comentario debe estar asociado a un vídeo existente");
        }

        return videoRepository.findById(comment.getVideo().getId())
            .orElseThrow(VideoNotFoundException::new);    
    }
}