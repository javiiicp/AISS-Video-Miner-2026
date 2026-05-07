package aiss.videominer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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

import aiss.videominer.model.Comment;
import aiss.videominer.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/videominer/comments")
@Tag(name = "Controlador Comentarios", description = "Operaciones para gestionar los comentarios")
@Validated
public class CommentController {

    @Autowired
    CommentService service;

    // GET http://localhost:8080/videominer/comments
    @Operation(summary = "Listar todos los comentarios", description = "Devuelve una lista paginada con todos los comentarios registrados en el sistema")
    @GetMapping
    public Page<Comment> findAll(
            @RequestParam(required = false) String text,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return service.findAll(text, paging);
    }

    // GET http://localhost:8080/videominer/comments/{id}
    @Operation(summary = "Obtener un comentario por ID", description = "Devuelve un comentario específico según su ID")
    @GetMapping("/{id}")
    public Comment findOne(@PathVariable String id) {
        return service.findOne(id);
    }

    // POST http://localhost:8080/videominer/comments
    @Operation(summary = "Crear un comentario", description = "Crea un comentario y lo asocia a un vídeo existente")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment create(@Valid @RequestBody Comment comment) {
        return service.create(comment);
    }

    // PUT http://localhost:8080/videominer/comments/{id}
    @Operation(summary = "Actualizar un comentario", description = "Modifica un comentario existente")
    @PutMapping("/{id}")
    public Comment update(@PathVariable String id, @Valid @RequestBody Comment updatedComment) {
        return service.update(id, updatedComment);
    }

    // DELETE http://localhost:8080/videominer/comments/{id}
    @Operation(summary = "Eliminar un comentario", description = "Borra permanentemente un comentario por su ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    // GET http://localhost:8080/videominer/comments/video/{videoId}
    @Operation(summary = "Obtener comentarios dado un vídeo", description = "Devuelve los comentarios asociados a un vídeo concreto")
    @GetMapping("/video/{videoId}")
    public Page<Comment> findByVideoId(
            @PathVariable String videoId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return service.findByVideo(videoId, paging);
    }
}

