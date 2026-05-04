package aiss.videominer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CaptionRepository;
import aiss.videominer.repository.VideoRepository;

@Service
@Transactional
public class VideoService {

    private final VideoRepository videoRepository;
    private final CaptionRepository captionRepository;
    private final UserService userService;

    public VideoService(VideoRepository videoRepository, CaptionRepository captionRepository, UserService userService) {
        this.videoRepository = videoRepository;
        this.captionRepository = captionRepository;
        this.userService = userService;
    }

    public Page<Video> findAll(String name, Pageable paging) {
        if (name != null && !name.isBlank()) {
            return videoRepository.findByNameContainingIgnoreCase(name, paging);
        }
        return videoRepository.findAll(paging);
    }

    public Video findOne(String id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new VideoNotFoundException("No se encontró el vídeo con id: " + id));
    }

    public Video create(Video video) {
        linkChildren(video);
        video.setAuthor(userService.findOrCreate(video.getAuthor()));
        return videoRepository.save(video);
    }

    public Video update(String id, Video updatedVideo) {
        findOne(id); // 404 check
        updatedVideo.setId(id);
        linkChildren(updatedVideo);
        updatedVideo.setAuthor(userService.findOrCreate(updatedVideo.getAuthor()));
        return videoRepository.save(updatedVideo);
    }

    private void linkChildren(Video video) {
        if (video.getComments() != null) {
            video.getComments().forEach(c -> c.setVideo(video));
        }
        if (video.getCaptions() != null) {
            video.getCaptions().forEach(c -> c.setVideo(video));
        }
    }

    public void delete(String id) {
        if (!videoRepository.existsById(id)) {
            throw new VideoNotFoundException("No se encontró el vídeo con id: " + id);
        }
        videoRepository.deleteById(id);
    }

    public Page<Caption> getCaptionsByVideo(String videoId, Pageable paging) {
        findOne(videoId);
        return captionRepository.findByVideo_Id(videoId, paging);
    }
}