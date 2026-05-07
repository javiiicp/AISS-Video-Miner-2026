package aiss.videominer.controller;

import aiss.videominer.model.Caption;
import aiss.videominer.model.Video;
import aiss.videominer.service.VideoService;
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
@RequestMapping("/videominer/videos")
@Tag(name = "2. Video Controller", description = "Controlador encargado de administrar los metadatos de los vídeos. " +
        "Permite gestionar la relación entre los autores (User), sus hilos de comentarios y sus subtítulos.")
@Validated
public class VideoController {

    @Autowired
    private VideoService service;

    /**
     * Lista y filtra los vídeos integrados en el sistema.
     * Devuelve una lista plana para cumplir con los requisitos de Postman.
     */
    @Operation(
        summary = "Listar, Filtrar y Paginar Vídeos",
        description = """
                      ### Gu\u00eda de Operaci\u00f3n del Buscador:
                      Este endpoint permite explorar todo el cat\u00e1logo de v\u00eddeos minados con capacidades de b\u00fasqueda avanzada:
                      1. **B\u00fasqueda por T\u00edtulo**: Usa el par\u00e1metro `name` para filtrar v\u00eddeos (ej. 'Alien').
                      2. **Paginaci\u00f3n**: 
                         - `page`: N\u00famero de p\u00e1gina que deseas consultar (empieza en 0).
                         - `size`: Cantidad de v\u00eddeos por p\u00e1gina (m\u00e1ximo 100).
                      3. **Ordenaci\u00f3n**: El par\u00e1metro `sortBy` permite organizar los resultados por atributos como `releaseTime`, `name` o `id`.
                      
                      **Nota t\u00e9cnica**: La respuesta es un Array JSON directo `[]` para que Postman pueda validar la longitud de los datos autom\u00e1ticamente."""
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa. Lista de vídeos recuperada.", 
                     content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Video.class)))),
        @ApiResponse(responseCode = "400", description = "Solicitud incorrecta. Revise los parámetros de paginación.")
    })
    @GetMapping
    public List<Video> findAll(
            @Parameter(description = "Texto para filtrar vídeos por su título.", example = "Alien") 
            @RequestParam(required = false) String name,
            @Parameter(description = "Índice de la página de resultados.", example = "0") 
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Número máximo de vídeos por página.", example = "10") 
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Atributo por el cual ordenar los resultados.", example = "releaseTime") 
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        // IMPORTANTE: Se usa .getContent() para devolver una lista plana y pasar los tests de Postman.
        return service.findAll(name, paging).getContent(); 
    }

    @Operation(
        summary = "Obtener Vídeo por ID", 
        description = "Recupera la información completa de un vídeo específico, incluyendo su autor, la fecha de publicación y sus listas de comentarios y subtítulos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vídeo localizado con éxito."),
        @ApiResponse(responseCode = "404", description = "No se ha encontrado ningún vídeo con el ID proporcionado.")
    })
    @GetMapping("/{id}")
    public Video findOne(
            @Parameter(description = "ID único del vídeo.", required = true, example = "21856") 
            @PathVariable String id) {
        return service.findOne(id);
    }

    @Operation(
        summary = "Crear Nuevo Vídeo", 
        description = "Registra un vídeo de forma manual en el sistema. Es necesario proporcionar un objeto autor válido. " +
                "Si el campo 'id' no se incluye, el sistema generará uno automáticamente.")
    @ApiResponse(responseCode = "201", description = "Vídeo creado satisfactoriamente.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Video create(
            @Parameter(description = "Objeto vídeo en formato JSON.", required = true) 
            @Valid @RequestBody Video video) {
        return service.create(video);
    }

    @Operation(
        summary = "Actualizar Vídeo Existente", 
        description = "Modifica los metadatos de un vídeo (nombre, descripción, fecha de lanzamiento).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vídeo actualizado correctamente."),
        @ApiResponse(responseCode = "404", description = "El vídeo que intenta actualizar no existe.")
    })
    @PutMapping("/{id}")
    public Video update(
            @Parameter(description = "ID del vídeo a modificar.", required = true) @PathVariable String id,
            @Parameter(description = "Datos actualizados del vídeo.", required = true) @Valid @RequestBody Video updatedVideo) {
        return service.update(id, updatedVideo);
    }

    @Operation(
        summary = "Eliminar Vídeo", 
        description = "Borra permanentemente un vídeo del sistema. Esta acción eliminará también sus subtítulos y comentarios asociados por borrado en cascada.")
    @ApiResponse(responseCode = "204", description = "Vídeo eliminado con éxito. Respuesta sin cuerpo.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "ID del vídeo que desea borrar.") @PathVariable String id) {
        service.delete(id);
    }

    @Operation(
        summary = "Consultar Subtítulos de un Vídeo", 
        description = """
                      ### Acceso Directo a Captions:
                      Este endpoint permite obtener exclusivamente la lista de subt\u00edtulos asociados a este v\u00eddeo concreto. Soporta par\u00e1metros de paginaci\u00f3n independientes.""")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de subtítulos del vídeo recuperada."),
        @ApiResponse(responseCode = "404", description = "El vídeo solicitado no existe.")
    })
    @GetMapping("/{id}/captions")
    public List<Caption> getCaptionsByVideo(
            @Parameter(description = "ID del vídeo padre.", required = true, example = "21856") @PathVariable String id,
            @Parameter(description = "Página.") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Tamaño.") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Orden.") @RequestParam(defaultValue = "id") String sortBy) {
        
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return service.getCaptionsByVideo(id, paging).getContent(); 
    }
}