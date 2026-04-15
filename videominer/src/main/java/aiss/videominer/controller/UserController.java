package aiss.videominer.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aiss.videominer.model.User;
import aiss.videominer.repository.UserRepository;

@RestController
@RequestMapping("/videominer/users")
public class UserController {

    @Autowired
    UserRepository repository;

    // GET http://localhost:8080/videominer/users
    @GetMapping
    public List<User> findAll() {
        return repository.findAll();
    }

    // GET http://localhost:8080/videominer/users/{id}
    @GetMapping("/{id}")
    public User findOne(@PathVariable String id) {
        Optional<User> user = repository.findById(id);
        return user.orElse(null);
    }
}