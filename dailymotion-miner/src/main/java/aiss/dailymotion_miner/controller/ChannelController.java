package aiss.dailymotion_miner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import aiss.dailymotion_miner.model.Channel;
import aiss.dailymotion_miner.service.ApiChannelService;
import aiss.dailymotion_miner.service.VideominerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/channels")
@Tag(name = "Canales (Dailymotion)", description = "Operaciones para extraer y sincronizar canales de Dailymotion")
public class ChannelController {

    @Autowired
    private ApiChannelService channelService;

    @Autowired
    private VideominerService videominerService;

    // GET http://localhost:8082/api/channels/{id}?maxVideos=10&maxPages=2
    @Operation(
        summary = "Obtener canal de Dailymotion", 
        description = "Extrae de forma interactiva la información de un canal de Dailymotion junto con sus vídeos, etiquetas (como comentarios) y subtítulos, sin persistirlos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Canal obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "El canal o la playlist no existe en Dailymotion")
    })
    @GetMapping("/{id}")
    public Channel getChannel(
            @Parameter(description = "Identificador único de la playlist/canal en Dailymotion", required = true, example = "x6m01b")
            @PathVariable String id,
            
            @Parameter(description = "Número máximo de vídeos a extraer del canal", example = "10")
            @RequestParam(defaultValue = "10") Integer maxVideos,
            
            @Parameter(description = "Número máximo de páginas de resultados a consultar", example = "2")
            @RequestParam(defaultValue = "2") Integer maxPages) {
        Channel channel = channelService.getChannelFromDailymotion(id, maxVideos, maxPages);
        if (channel == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Canal no encontrado en Dailymotion");
        }
        return channel;
    }

    // POST http://localhost:8082/api/channels/{id}?maxVideos=10&maxPages=2
    @Operation(
        summary = "Guardar canal en VideoMiner", 
        description = "Extrae toda la información de un canal de Dailymotion y la envía mediante una petición POST al microservicio central de VideoMiner para guardarla en la base de datos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Canal guardado exitosamente en VideoMiner"),
        @ApiResponse(responseCode = "404", description = "Canal no encontrado en Dailymotion"),
        @ApiResponse(responseCode = "500", description = "Error interno de comunicación con el servicio VideoMiner")
    })
    @PostMapping("/{id}")
    public ResponseEntity<Channel> postChannel(
            @Parameter(description = "Identificador único de la playlist/canal en Dailymotion", required = true, example = "x6m01b")
            @PathVariable String id,
            
            @Parameter(description = "Número máximo de vídeos a extraer del canal", example = "10")
            @RequestParam(defaultValue = "10") Integer maxVideos,
            
            @Parameter(description = "Número máximo de páginas de resultados a consultar", example = "2")
            @RequestParam(defaultValue = "2") Integer maxPages) {
        Channel channel = channelService.getChannelFromDailymotion(id, maxVideos, maxPages);
        if (channel == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Canal no encontrado en Dailymotion");
        }

        Channel saved = videominerService.saveChannel(channel);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}