package aiss.videominer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import aiss.videominer.exception.CaptionNotFoundException;
import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CaptionRepository;
import aiss.videominer.repository.VideoRepository;

@Service
@Transactional
public class CaptionService {

    private final CaptionRepository captionRepository;
    private final VideoRepository videoRepository;

    public CaptionService(CaptionRepository captionRepository, VideoRepository videoRepository) {
        this.captionRepository = captionRepository;
        this.videoRepository = videoRepository;
    }

    public Page<Caption> findAll(Pageable paging) {
        return captionRepository.findAll(paging);
    }

    public Caption findOne(String id) {
        return captionRepository.findById(id).orElseThrow(CaptionNotFoundException::new);
    }

    public Caption create(Caption caption) {
        Video video = resolveVideo(caption);
        caption.setVideo(video);
        return captionRepository.save(caption);
    }

    public Caption update(String id, Caption updatedCaption) {
        Caption caption = findOne(id);
        caption.setLink(updatedCaption.getLink());
        caption.setLanguage(updatedCaption.getLanguage());
        Video video = resolveVideo(updatedCaption);
        caption.setVideo(video);
        caption.setId(id);
        return captionRepository.save(caption);
    }

    public void delete(String id) {
        if (captionRepository.existsById(id)) {
            captionRepository.deleteById(id);
            return;
        }
        throw new CaptionNotFoundException();
    }

    public Page<Caption> findByVideo(String videoId, Pageable paging) {
        videoRepository.findById(videoId).orElseThrow(VideoNotFoundException::new);
        return captionRepository.findByVideo_Id(videoId, paging);
    }

    private Video resolveVideo(Caption caption) {
        if (caption.getVideo() == null || caption.getVideo().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El subtítulo debe referenciar a un vídeo existente");
        }
        return videoRepository.findById(caption.getVideo().getId()).orElseThrow(VideoNotFoundException::new);
    }
}
