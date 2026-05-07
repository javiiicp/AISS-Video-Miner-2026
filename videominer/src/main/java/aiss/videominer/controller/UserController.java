package aiss.videominer.controller;

import aiss.videominer.model.User;
import aiss.videominer.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/videominer/users")
@Tag(name = "5. User Controller", description = "Controlador para la administración de perfiles de creadores de contenido. " +
        "Este recurso unifica el concepto de 'Account' de PeerTube y 'Owner' de Dailymotion.")
@Validated
public class UserController {

    @Autowired
    private UserService service;

    /**
     * Recupera y filtra los perfiles de usuario registrados.
     * Devuelve una lista plana para compatibilidad total con Postman.
     */
    @Operation(
        summary = "Listar, Filtrar y Paginar Usuarios",
        description = """
                      ### Gu\u00eda de exploraci\u00f3n de autores:
                      Utiliza este endpoint para gestionar la base de datos de creadores integrados.
                      1. **B\u00fasqueda por Nombre**: El par\u00e1metro `name` permite localizar usuarios por coincidencia parcial.
                      2. **Sistema de Paginaci\u00f3n**:
                         - `page`: N\u00famero de p\u00e1gina (comienza en 0).
                         - `size`: Cantidad de usuarios por p\u00e1gina (m\u00e1ximo 100).
                      3. **Ordenaci\u00f3n**: Par\u00e1metro `sortBy` para organizar por `id` o `name`.
                      
                      **Nota t\u00e9cnica**: La respuesta es un array plano `[]` para que pm.expect(jsonData.length) en Postman funcione correctamente."""
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa. Lista de usuarios recuperada.", 
                     content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class)))),
        @ApiResponse(responseCode = "400", description = "Parámetros de consulta inválidos.")
    })
    @GetMapping
    public List<User> findAll(
            @Parameter(description = "Nombre o fragmento para filtrar usuarios.", example = "stux") 
            @RequestParam(required = false) String name,
            @Parameter(description = "Índice de la página de resultados.", example = "0") 
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Número de elementos por página.", example = "10") 
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Atributo por el cual ordenar los resultados.", example = "name") 
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        // IMPORTANTE: .getContent() para que Postman reciba la lista directa.
        return service.findAll(name, paging).getContent(); 
    }

    @Operation(
        summary = "Consultar Usuario por ID", 
        description = "Devuelve el perfil completo de un autor, incluyendo sus enlaces a fotos y perfiles externos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario localizado correctamente."),
        @ApiResponse(responseCode = "404", description = "No se ha encontrado ningún usuario con el ID proporcionado.")
    })
    @GetMapping("/{id}")
    public User findOne(
            @Parameter(description = "Identificador único del usuario (UUID o ID externo).", required = true, example = "ae4f8f09-5e5a") 
            @PathVariable String id) {
        return service.findOne(id);
    }

    @Operation(
        summary = "Crear Nuevo Usuario", 
        description = "Registra un nuevo autor de forma manual. Si el 'id' se omite, el sistema generará un UUID automático antes de la persistencia.")
    @ApiResponse(responseCode = "201", description = "Usuario creado y persistido con éxito.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(
            @Parameter(description = "Objeto usuario en formato JSON.", required = true) 
            @Valid @RequestBody User user) {
        return service.create(user);
    }

    @Operation(
        summary = "Actualizar Perfil de Usuario", 
        description = "Permite modificar los datos de un autor existente (nombre, link de perfil, link de imagen).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil de usuario actualizado."),
        @ApiResponse(responseCode = "404", description = "El usuario que intenta modificar no existe.")
    })
    @PutMapping("/{id}")
    public User update(
            @Parameter(description = "ID del usuario a modificar.", required = true) @PathVariable String id,
            @Parameter(description = "Datos actualizados del usuario.", required = true) @Valid @RequestBody User updatedUser) {
        return service.update(id, updatedUser);
    }

    @Operation(
        summary = "Eliminar Usuario", 
        description = "Borra definitivamente al usuario del sistema. Esta acción eliminará su perfil de la base de datos H2.")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado con éxito. Respuesta sin cuerpo.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "ID del usuario a eliminar.") @PathVariable String id) {
        service.delete(id);
    }
}