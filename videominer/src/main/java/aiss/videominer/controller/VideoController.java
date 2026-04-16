package aiss.videominer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import aiss.videominer.model.Video;
import aiss.videominer.repository.VideoRepository;

@RestController
@RequestMapping("/videominer/videos") // Añadido /videos para mayor orden
@Tag(name = "Video Controller", description = "Operaciones para gestionar los vídeos")
public class VideoController {

    @Autowired
    VideoRepository repository;

    @Operation(summary = "Listar vídeos", description = "Lista vídeos con paginación, ordenación y filtro por nombre")
    @GetMapping
    public Page<Video> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));

        if (name != null) {
            return repository.findByNameContainingIgnoreCase(name, paging);
        }
        return repository.findAll(paging);
    }

    @Operation(summary = "Obtener un vídeo por ID")
    @GetMapping("/{id}")
    public Video findOne(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video no encontrado"));
    }
}