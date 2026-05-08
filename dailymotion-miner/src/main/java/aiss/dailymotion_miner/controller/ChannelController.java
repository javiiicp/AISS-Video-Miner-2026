package aiss.dailymotion_miner.controller;

import aiss.dailymotion_miner.model.Channel;
import aiss.dailymotion_miner.service.ApiChannelService;
import aiss.dailymotion_miner.service.VideominerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/channels")
@Tag(name = "1. Extracción de Dailymotion", description = "Controlador ETL encargado de transformar listas de reproducción (playlists) de Dailymotion en Canales unificados.")
@Validated
public class ChannelController {

    @Autowired
    private ApiChannelService channelService;

    @Autowired
    private VideominerService videominerService;

    @Operation(
            summary = "Previsualizar Canal (Playlist) desde Dailymotion",
            description = "### Guía de Minería de Dailymotion:\n" +
                    "Este endpoint extrae una playlist y sus vídeos, transformándolos al modelo de datos central.\n\n" +
                    "1. **Transformación Social**: Dailymotion no expone comentarios públicos vía API gratuita; por ello, este minero **transforma los Tags de cada vídeo en objetos Comment** para preservar la integridad social en VideoMiner.\n" +
                    "2. **Filtrado in-memory**: Permite buscar vídeos específicos por nombre dentro de la playlist extraída.\n" +
                    "3. **Control de Flujo**: Gestiona el volumen de datos mediante `maxVideos` y parámetros de paginación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Canal extraído y mapeado correctamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Channel.class))),
            @ApiResponse(responseCode = "404", description = "La Playlist de Dailymotion no existe o es privada."),
            @ApiResponse(responseCode = "400", description = "Error en los parámetros de consulta (ej. página negativa).")
    })
    @GetMapping("/{id}")
    public Channel getChannel(
            @Parameter(description = "ID de la playlist de Dailymotion (ej. 'x174092').", required = true, example = "x174092")
            @PathVariable String id,
            @Parameter(description = "Límite total de vídeos a extraer de la plataforma.", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) Integer maxVideos,
            @Parameter(description = "Máximo de etiquetas (tags) a convertir en comentarios por vídeo.", example = "5")
            @RequestParam(defaultValue = "5") @Min(0) Integer maxComments,
            @Parameter(description = "Filtrar la lista de vídeos por coincidencia parcial en el título.", example = "tutorial")
            @RequestParam(required = false) String name,
            @Parameter(description = "Número de página para la visualización de vídeos.", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @Parameter(description = "Cantidad de vídeos por página.", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "Criterio de ordenación de los vídeos.", example = "releaseTime")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Dirección: 'asc' o 'desc'.", example = "desc")
            @RequestParam(defaultValue = "asc") String sortDir) {

        // Nota: Se asume que el servicio implementará la lógica de paginación/filtrado similar al de PeerTube.
        return channelService.getChannelFromDailymotion(id, maxVideos, maxComments, name, page, size, sortBy, sortDir);
    }

    @Operation(
            summary = "Minar y Persistir en VideoMiner",
            description = "Ejecuta el ciclo completo de Integración: **Extrae** de Dailymotion, **Transforma** los metadatos y **Carga** (POST) el resultado en la API central.\n\n" +
                    "**Garantía de Integridad**: El sistema asegura que los autores de Dailymotion se mapeen a usuarios únicos en VideoMiner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Canal minado y guardado exitosamente en el almacén central."),
            @ApiResponse(responseCode = "404", description = "No se pudo localizar el recurso de origen en Dailymotion."),
            @ApiResponse(responseCode = "500", description = "Error de conexión con el microservicio VideoMiner.")
    })
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Channel postChannel(
            @Parameter(description = "ID de la playlist a minar.", required = true)
            @PathVariable String id,
            @RequestParam(defaultValue = "10") @Min(1) Integer maxVideos,
            @RequestParam(defaultValue = "5") @Min(0) Integer maxComments,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        // 1. Extracción y Transformación (ET)
        Channel channel = channelService.getChannelFromDailymotion(id, maxVideos, maxComments, name, page, size, sortBy, sortDir);

        // 2. Carga en VideoMiner (L)
        if (channel != null) {
            videominerService.saveChannel(channel);
        }

        return channel;
    }
}