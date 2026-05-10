package aiss.videominer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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

import aiss.videominer.model.Comment;
import aiss.videominer.service.CommentService;
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

@RestController
@RequestMapping("/videominer/comments")
@Tag(name = "4. Comment Controller", description = "Controlador para la administración de la interacción social y etiquetas de los vídeos. " +
        "Importante: En la integración con Dailymotion, este recurso se utiliza para gestionar los 'tags' del vídeo.")
@Validated
public class CommentController {

    @Autowired
    private CommentService service;

    @Operation(
        summary = "Listar, Filtrar y Paginar Comentarios",
        description = """
                      ### Gu\u00eda de uso para desarrolladores:
                      Este endpoint centraliza la b\u00fasqueda de hilos de comentarios y etiquetas.
                      1. **Filtrado por Contenido**: Usa el par\u00e1metro `text` para buscar palabras clave dentro del cuerpo del comentario.
                      2. **Control de Flujo (Paginaci\u00f3n)**: 
                         - `page`: \u00cdndice de p\u00e1gina (desde 0).
                         - `size`: Cantidad de registros (m\u00e1ximo 100).
                      3. **Ordenaci\u00f3n**: Par\u00e1metro `sortBy` para organizar por `id`, `text` o `createdOn`.
                      
                      **Nota de Integraci\u00f3n**: Devuelve un array JSON directo `[]` para que las pruebas de longitud en Postman sean exitosas."""
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa. Lista de comentarios recuperada según criterios.", 
                     content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Comment.class)))),
        @ApiResponse(responseCode = "400", description = "Error en los parámetros de consulta proporcionados.")
    })
    @GetMapping
    public List<Comment> findAll(
            @Parameter(description = "Texto o palabra clave para filtrar el contenido del comentario.", example = "increíble") 
            @RequestParam(required = false) String text,
            @Parameter(description = "Número de página a recuperar.", example = "0") 
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Elementos por página.", example = "10") 
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Campo por el cual ordenar los resultados.", example = "createdOn") 
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return service.findAll(text, paging).getContent(); 
    }

    @Operation(
        summary = "Consultar Comentario por ID", 
        description = "Obtiene los detalles de un comentario o tag específico. Útil para verificar metadatos de creación.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comentario localizado con éxito."),
        @ApiResponse(responseCode = "404", description = "No se ha encontrado el comentario con el ID indicado.")
    })
    @GetMapping("/{id}")
    public Comment findOne(
            @Parameter(description = "ID único del comentario.", required = true, example = "3956") 
            @PathVariable String id) {
        return service.findOne(id);
    }

    @Operation(
        summary = "Publicar Nuevo Comentario/Tag", 
        description = "Crea un nuevo comentario y lo asocia a un vídeo ya persistido. Es obligatorio que el objeto JSON incluya la referencia al vídeo padre.")
    @ApiResponse(responseCode = "201", description = "Comentario creado y vinculado satisfactoriamente.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment create(
            @Parameter(description = "Objeto comentario en formato JSON.", required = true) 
            @Valid @RequestBody Comment comment) {
        return service.create(comment);
    }

    @Operation(
        summary = "Actualizar Comentario Existente", 
        description = "Permite modificar el texto o la fecha de un comentario/tag ya registrado en la base de datos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "El comentario ha sido actualizado correctamente."),
        @ApiResponse(responseCode = "404", description = "El recurso que intenta modificar no existe.")
    })
    @PutMapping("/{id}")
    public Comment update(
            @Parameter(description = "ID del comentario a modificar.", required = true) @PathVariable String id,
            @Parameter(description = "Datos actualizados del comentario.", required = true) @Valid @RequestBody Comment updatedComment) {
        return service.update(id, updatedComment);
    }

    @Operation(
        summary = "Eliminar Comentario", 
        description = "Borra permanentemente el comentario o tag del sistema. Esta acción no se puede deshacer.")
    @ApiResponse(responseCode = "204", description = "Comentario eliminado con éxito. Respuesta sin contenido.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "ID del comentario que desea eliminar.") @PathVariable String id) {
        service.delete(id);
    }

    @Operation(
        summary = "Listar Comentarios por Vídeo", 
        description = """
                      ### B\u00fasqueda Jer\u00e1rquica:
                      Recupera de forma paginada todos los comentarios o etiquetas que pertenecen a un v\u00eddeo concreto. Ideal para mostrar el hilo social debajo de un reproductor de v\u00eddeo.""")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Hilo de comentarios del vídeo recuperado."),
        @ApiResponse(responseCode = "404", description = "El vídeo padre solicitado no existe en el sistema.")
    })
    @GetMapping("/video/{videoId}")
    public List<Comment> findByVideoId(
            @Parameter(description = "ID del vídeo del cual obtener los comentarios.", required = true, example = "21856") @PathVariable String videoId,
            @Parameter(description = "Página.") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Tamaño.") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Orden.") @RequestParam(defaultValue = "id") String sortBy) {
        
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return service.findByVideo(videoId, paging).getContent(); 
    }
}