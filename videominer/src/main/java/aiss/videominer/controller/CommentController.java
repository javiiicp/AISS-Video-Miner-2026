package aiss.videominer.controller;



import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;


import aiss.videominer.model.Comment;
import aiss.videominer.repository.CommentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/videominer/comments")
@Tag(name = "Comment Controller", description = "Operaciones para gestionar los comentarios")
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

     @Autowired

    // GET http://localhost:8080/videominer/comments
    @Operation(
        summary = "Listar todos los comentarios", 
        description = "Devuelve una lista con todos los comentarios registrados en el sistema",
        tags = { "comment", "get"}
    )

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(contentSchema = Comment.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) })
    })

    @GetMapping
    public Page<Comment> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy) {
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return commentRepository.findAll(paging);
    }

    // GET http://localhost:8080/videominer/comments/{id}
    @Operation(summary = "Obtener un comentario por ID", description = "Devuelve un comentario específico según su ID")
    @GetMapping("/{id}")
    public Comment findOne(@PathVariable String id) {
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.orElse(null);
    }
    // GET http://localhost:8080/videominer/comments/video/{videoId}
    @Operation(summary = "Obtener comentarios dado un vídeo", description = "Devuelve los comentarios asociados a un vídeo concreto")
    @GetMapping("/video/{videoId}")
    public List<Comment> findByVideoId(@PathVariable String videoId) {
    return commentRepository.findByVideoId(videoId);
}
}

