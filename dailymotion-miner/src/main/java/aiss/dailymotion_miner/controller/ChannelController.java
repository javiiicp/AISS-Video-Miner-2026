package aiss.dailymotion_miner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import aiss.dailymotion_miner.exception.ChannelNotFoundException;
import aiss.dailymotion_miner.model.Channel;
import aiss.dailymotion_miner.service.ApiChannelService;
import aiss.dailymotion_miner.service.VideominerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/channels")
@Tag(name = "Canales (Dailymotion)", description = "Operaciones para extraer y sincronizar canales de Dailymotion")
public class ChannelController {

    @Autowired
    private ApiChannelService channelService;

    @Autowired
    private VideominerService videominerService;

    // GET http://localhost:8082/api/channels/{id}
    @Operation(
        summary = "Obtener canal de Dailymotion", 
        description = "Extrae de forma interactiva la información de un canal de Dailymotion junto con sus vídeos y metadatos transformados, sin persistirlos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Canal obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "El canal no existe en Dailymotion")
    })
    @GetMapping("/{id}")
    public Channel getChannel(
            @Parameter(description = "ID de la playlist/canal en Dailymotion", required = true, example = "x6m01b")
            @PathVariable String id,
            
            @Parameter(description = "Máximo de vídeos a extraer")
            @RequestParam(defaultValue = "10") @Min(1) Integer maxVideos,
            
            @Parameter(description = "Máximo de páginas de la API a consultar")
            @RequestParam(defaultValue = "2") @Min(1) Integer maxPages) {
        
        Channel channel = channelService.getChannelFromDailymotion(id, maxVideos, maxPages); //Con los cambios que hemos hecho, debería de lanzarse la excepción sin problema
        
        if (channel == null) {
            throw new ChannelNotFoundException(); //
        }
        
        return channel;
    }

    // POST http://localhost:8082/api/channels/{id}
    @Operation(
        summary = "Guardar canal en VideoMiner", 
        description = "Extrae la información de Dailymotion y la envía al microservicio VideoMiner para su persistencia."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Canal guardado exitosamente en VideoMiner"),
        @ApiResponse(description = "Canal no encontrado", responseCode = "404"),
        @ApiResponse(description = "Error de comunicación con VideoMiner", responseCode = "500")
    })
    @PostMapping("/{id}")
    public ResponseEntity<Channel> postChannel(
            @PathVariable String id,
            @RequestParam(defaultValue = "10") @Min(1) Integer maxVideos,
            @RequestParam(defaultValue = "2") @Min(1) Integer maxPages) {
        
        Channel channel = channelService.getChannelFromDailymotion(id, maxVideos, maxPages);
        
        if (channel == null) {
            throw new ChannelNotFoundException();
        }

        Channel saved = videominerService.saveChannel(channel);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}