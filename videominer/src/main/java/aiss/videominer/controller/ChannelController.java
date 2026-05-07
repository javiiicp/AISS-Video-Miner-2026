package aiss.videominer.controller;

import aiss.videominer.model.Channel;
import aiss.videominer.service.ChannelService;
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
@RequestMapping("/videominer/channels")
@Tag(name = "1. Gestión de Canales", description = "Controlador principal para la administración de canales multimedia. " +
        "Este servicio permite centralizar la información recolectada de PeerTube y Dailymotion.")
@Validated
public class ChannelController {

    @Autowired
    private ChannelService service;

    /**
     * Lista y filtra los canales registrados.
     * Devuelve una lista plana para cumplir con pm.expect(jsonData.length) de Postman.
     */
    @Operation(
        summary = "Listar, Filtrar y Paginar Canales",
        description = """
                      ### Gu\u00eda de Uso del Endpoint:
                      Este m\u00e9todo permite explorar el cat\u00e1logo de canales con tres capacidades avanzadas:
                      1. **Filtrado Din\u00e1mico**: Utiliza el par\u00e1metro `name` para realizar una b\u00fasqueda parcial (ej. 'tv').
                      2. **Paginaci\u00f3n Profesional**: 
                         - `page`: Indica el n\u00famero de p\u00e1gina que deseas ver (empieza en 0).
                         - `size`: Define cu\u00e1ntos canales quieres ver por p\u00e1gina (ej. 10).
                      3. **Ordenaci\u00f3n Personalizada**: Usa `sortBy` para ordenar por cualquier campo (id, name, createdTime).
                      
                      **Nota t\u00e9cnica**: Para asegurar que las pruebas de Postman funcionen, la respuesta es un array directo `[]`."""
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa. Se devuelve la lista de canales que coinciden con los criterios.", 
                     content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Channel.class)))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida. Compruebe que los parámetros de paginación sean correctos.")
    })
    @GetMapping
    public List<Channel> findAll(
            @Parameter(description = "Texto para filtrar canales por su nombre.", example = "tv") 
            @RequestParam(required = false) String name,
            @Parameter(description = "Índice de la página de resultados a recuperar (base 0).", example = "0") 
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Número máximo de canales a devolver en esta página (máximo 100).", example = "10") 
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Atributo del canal por el cual se desea ordenar la lista.", example = "id") 
            @RequestParam(defaultValue = "id") String sortBy) {
        
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        // IMPORTANTE: .getContent() extrae la lista plana de la página para que Postman no falle.
        return service.findAll(name, paging).getContent(); 
    }

    @Operation(
        summary = "Consultar Detalle de un Canal", 
        description = "Recupera toda la información de un canal mediante su identificador único. " +
                "La respuesta incluye la lista de vídeos asociados, sus comentarios y sus subtítulos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Canal localizado con éxito."),
        @ApiResponse(responseCode = "404", description = "No se ha encontrado ningún canal con el ID suministrado.")
    })
    @GetMapping("/{id}")
    public Channel findOne(
            @Parameter(description = "ID único del canal (puede ser numérico como '80' o un UUID).", required = true, example = "80") 
            @PathVariable String id) {
        return service.findOne(id);
    }

    @Operation(
        summary = "Registrar Nuevo Canal (Integración)", 
        description = "Crea un nuevo canal en el sistema. Si no proporcionas un `id` en el cuerpo del JSON, " +
                "el servidor generará uno automáticamente siguiendo el estándar UUID.")
    @ApiResponse(responseCode = "201", description = "Canal creado y persistido satisfactoriamente en la base de datos H2.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Channel create(
            @Parameter(description = "Objeto JSON completo con los datos del canal y sus vídeos.", required = true) 
            @Valid @RequestBody Channel channel) {
        return service.create(channel);
    }

    @Operation(
        summary = "Actualizar Canal Existente", 
        description = "Permite modificar los atributos de un canal ya registrado. Se requiere el ID en la URL y el objeto actualizado en el cuerpo de la petición.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "El canal ha sido actualizado correctamente."),
        @ApiResponse(responseCode = "404", description = "El canal que intenta actualizar no existe en el sistema.")
    })
    @PutMapping("/{id}")
    public Channel update(
            @Parameter(description = "ID del canal a modificar.", required = true) @PathVariable String id,
            @Parameter(description = "Datos actualizados del canal.", required = true) @Valid @RequestBody Channel updatedChannel) {
        return service.update(id, updatedChannel);
    }

    @Operation(
        summary = "Borrar Canal y Contenidos", 
        description = "Elimina de forma permanente un canal del sistema. **Aviso**: Esta operación realiza un borrado en cascada, " +
                "eliminando también todos los vídeos, comentarios y subtítulos vinculados.")
    @ApiResponse(responseCode = "204", description = "Canal eliminado con éxito. No se devuelve contenido en el cuerpo.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "ID del canal que desea eliminar definitivamente.") @PathVariable String id) {
        service.delete(id);
    }
}