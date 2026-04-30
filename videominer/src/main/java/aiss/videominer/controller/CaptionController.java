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
import aiss.videominer.service.CaptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/videominer/captions")
@Tag(name = "Caption Controller", description = "Operaciones para gestionar los subtítulos")
public class CaptionController {

    @Autowired
    CaptionService service;

    // GET http://localhost:8080/videominer/captions
    @Operation(summary = "Listar subtítulos", description = "Devuelve una lista paginada con todos los subtítulos registrados")
    @GetMapping
    public Page<Caption> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return service.findAll(paging);
    }

    // GET http://localhost:8080/videominer/captions/{id}
    @Operation(summary = "Obtener un subtítulo por ID", description = "Devuelve un subtítulo específico según su ID")
    @GetMapping("/{id}")
    public Caption findCaptionById(@PathVariable String id) {
        return service.findOne(id);
    }

    // POST http://localhost:8080/videominer/captions
    @Operation(summary = "Crear un subtítulo", description = "Crea un subtítulo y lo asocia a un vídeo existente")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Caption create(@Valid @RequestBody Caption caption) {
        return service.create(caption);
    }

    // PUT http://localhost:8080/videominer/captions/{id}
    @Operation(summary = "Actualizar un subtítulo", description = "Modifica un subtítulo existente")
    @PutMapping("/{id}")
    public Caption update(@PathVariable String id, @Valid @RequestBody Caption updatedCaption) {
        return service.update(id, updatedCaption);
    }

    // DELETE http://localhost:8080/videominer/captions/{id}
    @Operation(summary = "Eliminar un subtítulo", description = "Borra permanentemente un subtítulo por su ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    // GET http://localhost:8080/videominer/captions/video/{videoId}
    @Operation(summary = "Obtener subtítulos por vídeo", description = "Devuelve subtítulos asociados a un vídeo concreto")
    @GetMapping("/video/{videoId}")
    public Page<Caption> findByVideoId(
            @PathVariable String videoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return service.findByVideo(videoId, paging);
    }
}