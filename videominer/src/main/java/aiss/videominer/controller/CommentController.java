package aiss.videominer.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import aiss.videominer.model.Comment;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CommentRepository;
import aiss.videominer.repository.VideoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/videominer/comments")
@Tag(name = "Comment Controller", description = "Operaciones para gestionar los comentarios")
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    VideoRepository videoRepository;

    // GET http://localhost:8080/videominer/comments
    @Operation(summary = "Listar todos los comentarios", description = "Devuelve una lista paginada con todos los comentarios registrados en el sistema")
    @GetMapping
    public Page<Comment> findAll(
            @RequestParam(required = false) String text,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));

        if (text != null) {
            return commentRepository.findByTextContainingIgnoreCase(text, paging);
        }
        return commentRepository.findAll(paging);
    }

    // GET http://localhost:8080/videominer/comments/{id}
    @Operation(summary = "Obtener un comentario por ID", description = "Devuelve un comentario específico según su ID")
    @GetMapping("/{id}")
    public Comment findOne(@PathVariable String id) {
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentario no encontrado"));
    }

    // POST http://localhost:8080/videominer/comments
    @Operation(summary = "Crear un comentario", description = "Crea un comentario y lo asocia a un vídeo existente")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment create(@Valid @RequestBody Comment comment) {
        if (comment.getVideo() == null || comment.getVideo().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El comentario debe estar asociado a un vídeo existente");
        }

        Video video = videoRepository.findById(comment.getVideo().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vídeo no encontrado"));

        comment.setVideo(video);
        return commentRepository.save(comment);
    }

    // PUT http://localhost:8080/videominer/comments/{id}
    @Operation(summary = "Actualizar un comentario", description = "Modifica un comentario existente")
    @PutMapping("/{id}")
    public Comment update(@PathVariable String id, @Valid @RequestBody Comment updatedComment) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentario no encontrado"));

        comment.setText(updatedComment.getText());
        comment.setCreatedOn(updatedComment.getCreatedOn());

        if (updatedComment.getVideo() == null || updatedComment.getVideo().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El comentario debe estar asociado a un vídeo existente");
        }

        Video video = videoRepository.findById(updatedComment.getVideo().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vídeo no encontrado"));

        comment.setVideo(video);
        comment.setId(id);
        return commentRepository.save(comment);
    }

    // DELETE http://localhost:8080/videominer/comments/{id}
    @Operation(summary = "Eliminar un comentario", description = "Borra permanentemente un comentario por su ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentario no encontrado");
        }
    }

    // GET http://localhost:8080/videominer/comments/video/{videoId}
    @Operation(summary = "Obtener comentarios dado un vídeo", description = "Devuelve los comentarios asociados a un vídeo concreto")
    @GetMapping("/video/{videoId}")
    public Page<Comment> findByVideoId(
            @PathVariable String videoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        videoRepository.findById(videoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vídeo no encontrado"));
        return commentRepository.findByVideo_Id(videoId, paging);
    }
}

