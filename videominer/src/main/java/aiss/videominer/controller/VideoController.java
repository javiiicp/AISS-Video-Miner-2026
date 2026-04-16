package aiss.videominer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import aiss.videominer.model.Video;
import aiss.videominer.repository.VideoRepository;

@RestController
@RequestMapping("/videominer")
public class VideoController {

    private final VideoRepository videoRepository;
    @Autowired
    public VideoController(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

     @GetMapping
    public List<Video> findAll() {
        return videoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Video findOne(@PathVariable String id) {
    Video video = videoRepository.findOneById(id);
    if (video == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Video no encontrado");
    }
    return video;
    }
}