package aiss.videominer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import aiss.videominer.model.Caption;
import aiss.videominer.repository.CaptionRepository;


@RestController
@RequestMapping("/videominer/captions")
public class CaptionController {

    @Autowired
    CaptionRepository captionRepository;


    // GET http://localhost:8080/videominer/captions
    @GetMapping
    public List<Caption> findAll() {
        return captionRepository.findAll();
    }

    // GET http://localhost:8080/videominer/captions/{id}
    @GetMapping("/{id}")
    public Caption findCaptionById(@PathVariable String id){
        return captionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtítulo no encontrado"));
    }
    // GET http://localhost:8080/videominer/captions/video/{videoId}
    @GetMapping("/video/{videoId}")
    public List<Caption> findByVideoId(@PathVariable String videoId) {
        return captionRepository.findByVideoId(videoId);
    }
}