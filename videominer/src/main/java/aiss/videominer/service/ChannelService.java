package aiss.videominer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aiss.videominer.exception.ChannelNotFoundException;
import aiss.videominer.model.Channel;
import aiss.videominer.model.Video;
import aiss.videominer.repository.ChannelRepository;

@Service
@Transactional
public class ChannelService {

    private final ChannelRepository repository;
    private final UserService userService;

    public ChannelService(ChannelRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public Page<Channel> findAll(String name, Pageable paging) {
        if (name != null && !name.isBlank()) {
            return repository.findByNameContainingIgnoreCase(name, paging);
        }
        return repository.findAll(paging);
    }

    public Channel findOne(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ChannelNotFoundException("No se encontró el canal con id: " + id));
    }

    public Channel create(Channel channel) {
        normalizeChannelData(channel);
        return repository.save(channel);
    }

    public Channel update(String id, Channel updatedChannel) {
        findOne(id); // Verifica que existe o lanza 404
        updatedChannel.setId(id);
        normalizeChannelData(updatedChannel);
        return repository.save(updatedChannel);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ChannelNotFoundException("No se encontró el canal con id: " + id);
        }
        repository.deleteById(id);
    }

    private void normalizeChannelData(Channel channel) {
        if (channel == null || channel.getVideos() == null) return;

        for (Video video : channel.getVideos()) {
            if (video.getAuthor() != null) {
                video.setAuthor(userService.findOrCreate(video.getAuthor()));
            }
            if (video.getComments() != null) {
                video.getComments().forEach(comment -> comment.setVideo(video));
            }
            if (video.getCaptions() != null) {
                video.getCaptions().forEach(caption -> caption.setVideo(video));
            }
        }
    }
}