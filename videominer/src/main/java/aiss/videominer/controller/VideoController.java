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

import aiss.videominer.model.Caption;
import aiss.videominer.model.Video;
import aiss.videominer.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/videominer/videos")
@Tag(name = "Vídeos", description = "Operaciones para gestionar los vídeos")
@Validated
public class VideoController {

    @Autowired
    VideoService service;

    @Operation(summary = "Listar vídeos", description = "Lista vídeos con paginación y filtro por nombre")
    @GetMapping
    public Page<Video> findAll(
            @Parameter(description = "Nombre o parte del título del vídeo") @RequestParam(required = false) String name,
            @Parameter(description = "Página") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Atributo de orden (id, name, releaseTime)") @RequestParam(defaultValue = "id") String sortBy) {

        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return service.findAll(name, paging);
    }

    @Operation(summary = "Obtener un vídeo por ID")
    @GetMapping("/{id}")
    public Video findOne(@PathVariable String id) {
        return service.findOne(id);
    }

    @Operation(summary = "Crear un nuevo vídeo", description = "Registra un vídeo manualmente vinculándolo a un autor")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Video create(@Valid @RequestBody Video video) {
        return service.create(video);
    }

    @Operation(summary = "Actualizar un vídeo")
    @PutMapping("/{id}")
    public Video update(@PathVariable String id, @Valid @RequestBody Video updatedVideo) {
         return service.update(id, updatedVideo);
    }

    @Operation(summary = "Eliminar un vídeo")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @Operation(summary = "Obtener subtítulos de un vídeo", description = "Devuelve los subtítulos asociados a un vídeo específico")
    @GetMapping("/{id}/captions")
    public Page<Caption> getCaptionsByVideo(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return service.getCaptionsByVideo(id, paging);
    }
}