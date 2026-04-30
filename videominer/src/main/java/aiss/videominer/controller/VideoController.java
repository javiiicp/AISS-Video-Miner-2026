package aiss.videominer.controller;

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

import aiss.videominer.model.Caption;
import aiss.videominer.model.Video;
import aiss.videominer.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/videominer/videos") // Añadido /videos para mayor orden
@Tag(name = "Video Controller", description = "Operaciones para gestionar los vídeos")
public class VideoController {

    @Autowired
    VideoService service;

    // GET http://localhost:8080/videominer/videos
    @Operation(summary = "Listar vídeos", description = "Lista vídeos con paginación, ordenación y filtro por nombre")
    @GetMapping
    public Page<Video> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return service.findAll(name, paging);
    }

    // GET http://localhost:8080/videominer/videos/{id}
    @Operation(summary = "Obtener un vídeo por ID")
    @GetMapping("/{id}")
    public Video findOne(@PathVariable String id) {
        return service.findOne(id);
    }

    // POST http://localhost:8080/videominer/videos
    @Operation(summary = "Crear un nuevo vídeo", description = "Crea un vídeo manualmente en el sistema")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Video create(@Valid @RequestBody Video video) {
        return service.create(video);
    }

    // PUT http://localhost:8080/videominer/videos/{id}
    @Operation(summary = "Actualizar un vídeo", description = "Modifica los datos de un vídeo existente")
    @PutMapping("/{id}")
    public Video update(@PathVariable String id,@Valid @RequestBody Video updatedVideo) {
         return service.update(id, updatedVideo);
    }

    // DELETE http://localhost:8080/videominer/videos/{id}
    @Operation(summary = "Eliminar un vídeo", description = "Borra permanentemente un vídeo por su ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    // GET http://localhost:8080/videominer/videos/{id}/captions
    @GetMapping("/{id}/captions")
    @Operation(summary = "Obtener subtítulos de un vídeo con paginación")
    public Page<Caption> getCaptionsByVideo(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return service.getCaptionsByVideo(id, paging);
    }
}