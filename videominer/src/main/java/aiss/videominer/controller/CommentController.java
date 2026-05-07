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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aiss.videominer.model.Comment;
import aiss.videominer.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/videominer/comments")
@Tag(name = "Comentarios", description = "Operaciones para gestionar comentarios")
@Validated
public class CommentController {

    @Autowired
    CommentService service;

    @Operation(summary = "Listar todos los comentarios", description = "Búsqueda global paginada de comentarios")
    @GetMapping
    public Page<Comment> findAll(
            @Parameter(description = "Filtrar por texto contenido en el comentario") @RequestParam(required = false) String text,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return service.findAll(text, paging);
    }

    @Operation(summary = "Obtener un comentario por ID")
    @GetMapping("/{id}")
    public Comment findOne(@PathVariable String id) {
        return service.findOne(id);
    }

    @Operation(summary = "Crear un comentario", description = "Asocia un comentario a un vídeo existente")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment create(@Valid @RequestBody Comment comment) {
        return service.create(comment);
    }

    @Operation(summary = "Eliminar un comentario")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @Operation(summary = "Obtener comentarios de un vídeo", description = "Usa la URI /video/{videoId} para listar comentarios vinculados")
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