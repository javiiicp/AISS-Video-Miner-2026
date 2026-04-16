package aiss.videominer.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aiss.videominer.model.User;
import aiss.videominer.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/videominer/users")
@Tag(name = "User Controller", description = "Operaciones para gestionar los usuarios")
public class UserController {

    @Autowired
    UserRepository repository;

    // GET http://localhost:8080/videominer/users
    @Operation(summary = "Listar todos los usuarios", description = "Devuelve una lista con todos los usuarios registrados en el sistema")
    @GetMapping
    public Page<User> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy) {
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return repository.findAll(paging);
    }

    // GET http://localhost:8080/videominer/users/{id}
    @Operation(summary = "Obtener un usuario por ID", description = "Devuelve un usuario específico según su ID")
    @GetMapping("/{id}")
    public User findOne(@PathVariable String id) {
        Optional<User> user = repository.findById(id);
        return user.orElse(null);
    }
}