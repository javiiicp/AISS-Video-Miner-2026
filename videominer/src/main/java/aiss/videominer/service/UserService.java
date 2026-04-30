package aiss.videominer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import aiss.videominer.exception.UserNotFoundException;
import aiss.videominer.model.User;
import aiss.videominer.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Page<User> findAll(String name, Pageable paging) {
        if (name != null && !name.isBlank()) {
            return repository.findByNameContainingIgnoreCase(name, paging);
        }
        return repository.findAll(paging);
    }

    public User findOne(String id) {
        return repository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User create(User user) {
        return repository.save(user);
    }

    public User update(String id, User updatedUser) {
        User user = findOne(id);
        user.setName(updatedUser.getName());
        user.setUser_link(updatedUser.getUser_link());
        user.setPicture_link(updatedUser.getPicture_link());
        user.setVideos(updatedUser.getVideos());
        return repository.save(user);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException();
        }
        repository.deleteById(id);
    }
}
