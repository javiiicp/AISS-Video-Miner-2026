package aiss.videominer.controller;

import java.util.List;

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

import aiss.videominer.exception.CaptionNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.repository.CaptionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/videominer/captions")
@Tag(name = "Caption Controller", description = "Operaciones para gestionar los subtítulos")
public class CaptionController {

    @Autowired
    CaptionRepository captionRepository;


    // GET http://localhost:8080/videominer/captions
    @Operation(summary = "Listar subtítulos", description = "Devuelve una lista con todos los subtítulos registrados")
    @GetMapping
    public List<Caption> findAll() {
        return captionRepository.findAll();
    }

    // GET http://localhost:8080/videominer/captions/{id}
    @Operation(summary = "Obtener un subtítulo por ID", description = "Devuelve un subtítulo específico según su ID")
    @GetMapping("/{id}")
    public Caption findCaptionById(@PathVariable String id) throws CaptionNotFoundException {
        Optional<Caption> caption = captionRepository.findById(id);
        if (!caption.isPresent()){
            throw new CaptionNotFoundException();
        }
        return caption.get();
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
        return captionRepository.findByVideo_Id(videoId, paging);
    }
}