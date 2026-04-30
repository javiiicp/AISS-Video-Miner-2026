package aiss.videominer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aiss.videominer.model.User;
import aiss.videominer.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/videominer/users")
@Tag(name = "Controlador Usuarios", description = "Operaciones para gestionar los usuarios")
public class UserController {

    @Autowired
    UserService service;

    // GET http://localhost:8080/videominer/users
    @Operation(summary = "Listar todos los usuarios", description = "Devuelve una lista con todos los usuarios registrados en el sistema")
    @GetMapping
    public Page<User> findAll(
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy) {
        
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return service.findAll(name, paging);
    }

    // GET http://localhost:8080/videominer/users/{id}
    @Operation(summary = "Obtener un usuario por ID", description = "Devuelve un usuario específico según su ID")
    @GetMapping("/{id}")
    public User findOne(@PathVariable String id) {
        return service.findOne(id);
    }

    // POST http://localhost:8080/videominer/users
    @Operation(summary = "Crear un nuevo usuario", description = "Crea un usuario manualmente en el sistema")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return service.create(user);
    }

    // PUT http://localhost:8080/videominer/users/{id}
    @Operation(summary = "Actualizar un usuario", description = "Modifica los datos de un usuario existente")
    @PutMapping("/{id}")
    public User update(@PathVariable String id, @Valid @RequestBody User updatedUser) {
        return service.update(id, updatedUser);
    }

    // DELETE http://localhost:8080/videominer/users/{id}
    @Operation(summary = "Eliminar un usuario", description = "Borra permanentemente un usuario por su ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}