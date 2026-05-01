package aiss.videominer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.model.User;
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
        return videoRepository.findById(id).orElseThrow(VideoNotFoundException::new);
    }

    public void delete(String id) {
        if (!videoRepository.existsById(id)) {
            throw new VideoNotFoundException();
        }
        videoRepository.deleteById(id);
    }

    public Video create(Video video) {
        video.setAuthor(resolveAuthor(video.getAuthor()));
        return videoRepository.save(video);
    }

    public Video update(String id, Video updatedVideo) {
        Video video = findOne(id);
        video.setName(updatedVideo.getName());
        video.setDescription(updatedVideo.getDescription());
        video.setAuthor(resolveAuthor(updatedVideo.getAuthor()));
        video.setReleaseTime(updatedVideo.getReleaseTime());
        video.setCaptions(updatedVideo.getCaptions());
        video.setComments(updatedVideo.getComments());
        return videoRepository.save(video);
    }

    public Page<Caption> getCaptionsByVideo(String videoId, Pageable paging) {
        findOne(videoId);
        return captionRepository.findByVideo_Id(videoId, paging);
    }

    private User resolveAuthor(User author) {
        return userService.findOrCreate(author);
    }
}
