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
import org.springframework.web.server.ResponseStatusException;

import aiss.videominer.model.Channel;
import aiss.videominer.repository.ChannelRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/videominer/channels")
@Tag(name = "Channel Controller", description = "Operaciones para gestionar los canales")
public class ChannelController {

	@Autowired
	ChannelRepository repository;

	// GET http://localhost:8080/videominer/channels
	@Operation(summary = "Listar canales", description = "Lista canales con paginación, ordenación y filtro por nombre")
	@GetMapping
	public Page<Channel> findAll(
			@RequestParam(required = false) String name,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "id") String sortBy) {

		Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));

		if (name != null) {
			return repository.findByNameContainingIgnoreCase(name, paging);
		}

		return repository.findAll(paging);
	}

	// GET http://localhost:8080/videominer/channels/{id}
	@Operation(summary = "Obtener un canal por ID")
	@GetMapping("/{id}")
	public Channel findOne(@PathVariable String id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Canal no encontrado"));
	}

	// POST http://localhost:8080/videominer/channels
	@Operation(summary = "Crear un nuevo canal", description = "Crea un canal manualmente en el sistema")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Channel create(@Valid @RequestBody Channel channel) {
		return repository.save(channel);
	}

	// PUT http://localhost:8080/videominer/channels/{id}
	@Operation(summary = "Actualizar un canal", description = "Modifica los datos de un canal existente")
	@PutMapping("/{id}")
	public Channel update(@PathVariable String id, @Valid @RequestBody Channel updatedChannel) {
		Channel channel = repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Canal no encontrado"));

		channel.setName(updatedChannel.getName());
		channel.setDescription(updatedChannel.getDescription());
		channel.setCreatedTime(updatedChannel.getCreatedTime());
		channel.setVideos(updatedChannel.getVideos());

		return repository.save(channel);
	}

	// DELETE http://localhost:8080/videominer/channels/{id}
	@Operation(summary = "Eliminar un canal", description = "Borra permanentemente un canal por su ID")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable String id) {
		if (repository.existsById(id)) {
			repository.deleteById(id);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Canal no encontrado");
		}
	}
}
