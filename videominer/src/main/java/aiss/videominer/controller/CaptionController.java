package aiss.videominer.controller;

import aiss.videominer.model.Caption;
import aiss.videominer.service.CaptionService;
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
@RequestMapping("/videominer/captions")
@Tag(name = "3. Gestión de Subtítulos", description = "Controlador encargado de administrar las transcripciones y traducciones (Captions). " +
        "Nota: En la integración con Dailymotion, este recurso equivale a los 'Subtitles' del vídeo.")
@Validated
public class CaptionController {

    @Autowired
    private CaptionService service;

    /**
     * Recupera todos los subtítulos del sistema con soporte para paginación profesional.
     */
    @Operation(
        summary = "Listar y Paginar todos los Subtítulos",
        description = """
                      ### Guía de navegación:
                      Este endpoint permite explorar el catálogo global de subtítulos. Es ideal para auditorías de contenido o gestión masiva.
                      1. **Paginación**: Usa `page` (empezando en 0) y `size` (máximo 100) para segmentar la respuesta.
                      2. **Ordenación**: El parámetro `sortBy` permite organizar los resultados por campos como `language` o `id`.
                      
                      **Compatibilidad**: Devuelve una lista plana para que Postman pueda validar la longitud de los datos directamente."""
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa. Lista de subtítulos recuperada.", 
                     content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Caption.class)))),
        @ApiResponse(responseCode = "400", description = "Parámetros de paginación o de ordenación incorrectos.")
    })
    @GetMapping
    public List<Caption> findAll(
            @Parameter(description = "Número de la página que se desea visualizar.", example = "0") 
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Número de elementos por página (límite de 100).", example = "10") 
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Atributo por el cual ordenar la lista.", example = "language") 
            @RequestParam(defaultValue = "id") String sortBy) {
        
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return service.findAll(paging).getContent(); // Retorna List para Postman
    }

    @Operation(
        summary = "Consultar Subtítulo por ID", 
        description = "Obtiene la información técnica de un subtítulo individual, incluyendo el enlace al archivo y el idioma asignado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Subtítulo localizado con éxito."),
        @ApiResponse(responseCode = "404", description = "No existe ningún subtítulo con el ID proporcionado.")
    })
    @GetMapping("/{id}")
    public Caption findCaptionById(
            @Parameter(description = "Identificador único del subtítulo.", required = true, example = "1") 
            @PathVariable String id) {
        return service.findOne(id);
    }

    @Operation(
        summary = "Registrar un Nuevo Subtítulo", 
        description = "Crea un subtítulo y lo vincula a un vídeo existente. El cuerpo de la petición debe incluir el objeto `video` o su ID para mantener la integridad referencial.")
    @ApiResponse(responseCode = "201", description = "Subtítulo creado satisfactoriamente.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Caption create(
            @Parameter(description = "Datos del subtítulo en formato JSON.", required = true) 
            @Valid @RequestBody Caption caption) {
        return service.create(caption);
    }

    @Operation(
        summary = "Actualizar Subtítulo Existente", 
        description = "Permite modificar propiedades como el idioma o el enlace de descarga del subtítulo.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Subtítulo actualizado correctamente."),
        @ApiResponse(responseCode = "404", description = "El recurso que intenta actualizar no existe.")
    })
    @PutMapping("/{id}")
    public Caption update(
            @Parameter(description = "ID del subtítulo a modificar.", required = true) @PathVariable String id,
            @Parameter(description = "Objeto con los datos actualizados.", required = true) @Valid @RequestBody Caption updatedCaption) {
        return service.update(id, updatedCaption);
    }

    @Operation(
        summary = "Eliminar Subtítulo", 
        description = "Borra de forma permanente un subtítulo de la base de datos.")
    @ApiResponse(responseCode = "204", description = "Subtítulo eliminado con éxito. Respuesta sin cuerpo.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "ID del subtítulo a eliminar.") @PathVariable String id) {
        service.delete(id);
    }

    @Operation(
        summary = "Listar Subtítulos de un Vídeo Específico", 
        description = """
                      ### Filtro por V\u00eddeo:
                      Este endpoint es fundamental para obtener todos los idiomas disponibles de un v\u00eddeo concreto. Soporta los mismos par\u00e1metros de paginaci\u00f3n que el listado global.""")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de subtítulos del vídeo recuperada."),
        @ApiResponse(responseCode = "404", description = "El vídeo solicitado no existe en el sistema.")
    })
    @GetMapping("/video/{videoId}")
    public List<Caption> findByVideoId(
            @Parameter(description = "ID del vídeo padre.", required = true, example = "21856") @PathVariable String videoId,
            @Parameter(description = "Página.") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Tamaño.") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Orden.") @RequestParam(defaultValue = "id") String sortBy) {
        
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        return service.findByVideo(videoId, paging).getContent(); // Retorna List para Postman
    }
}