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
        return repository.findById(id).orElseThrow(ChannelNotFoundException::new);
    }

    public Channel create(Channel channel) {
        normalizeVideoAuthors(channel);
        return repository.save(channel);
    }

    public Channel update(String id, Channel updatedChannel) {
        Channel channel = findOne(id);
        channel.setName(updatedChannel.getName());
        channel.setDescription(updatedChannel.getDescription());
        channel.setCreatedTime(updatedChannel.getCreatedTime());
        channel.setVideos(updatedChannel.getVideos());
        normalizeVideoAuthors(channel);
        return repository.save(channel);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ChannelNotFoundException();
        }
        repository.deleteById(id);
    }

    private void normalizeVideoAuthors(Channel channel) {
        if (channel == null || channel.getVideos() == null) {
            return;
        }

        for (Video video : channel.getVideos()) {
            if (video != null) {
                video.setAuthor(userService.findOrCreate(video.getAuthor()));
            }
        }
    }
}
