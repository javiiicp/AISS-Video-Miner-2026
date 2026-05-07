package aiss.videominer.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import aiss.videominer.exception.UserNotFoundException;
import aiss.videominer.model.User;
import aiss.videominer.repository.UserRepository;

@Service
@Transactional
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
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario con id: " + id));
    }

    public User create(User user) {
        return repository.save(user);
    }

    public User findOrCreate(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no puede ser nulo");
        }

        // Generar ID automáticamente si es null o está vacío
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        }

        return repository.findById(user.getId())
                .orElseGet(() -> repository.save(user));
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
            throw new UserNotFoundException("No se encontró el usuario con id: " + id);
        }
        repository.deleteById(id);
    }
}