package aiss.peertube_miner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aiss.peertube_miner.model.Channel;
import aiss.peertube_miner.service.ApiChannelService;
import aiss.peertube_miner.service.VideoMinerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/channels")
@Tag(name = "PeerTube Channels", description = "Extracción y transformación de canales de PeerTube hacia VideoMiner")
@Validated
public class ChannelController {

    @Autowired
    private ApiChannelService channelService;

    @Autowired
    private VideoMinerService videoMinerService;

    @Operation(
            summary = "Obtener canal de PeerTube", 
            description = "Extrae los metadatos de un canal y sus vídeos. Los parámetros de filtrado y paginación afectan exclusivamente a la lista de vídeos incluida en el canal."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Canal obtenido con éxito"),
            @ApiResponse(responseCode = "404", description = "El nombre (handle) del canal no existe en PeerTube")
    })
    @GetMapping("/{handle}")
    public Channel getChannel(
            @Parameter(description = "Nombre corto o 'handle' del canal en PeerTube (ej. 'aiss_channel')", example = "aiss_channel") 
            @PathVariable String handle,
            
            @Parameter(description = "Número máximo de vídeos a extraer de la plataforma") 
            @RequestParam(defaultValue = "10") @Min(1) Integer maxVideos,
            
            @Parameter(description = "Número máximo de comentarios a recuperar por cada vídeo") 
            @RequestParam(defaultValue = "2") @Min(0) Integer maxComments,
            
            @Parameter(description = "Filtrar vídeos por título (búsqueda parcial)") 
            @RequestParam(required = false) String name,
            
            @Parameter(description = "Número de página para la lista de vídeos (empieza en 0)") 
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            
            @Parameter(description = "Número de vídeos por página") 
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            
            @Parameter(description = "Atributo de ordenación de los vídeos (id, name, releaseTime)") 
            @RequestParam(defaultValue = "id") String sortBy,
            
            @Parameter(description = "Dirección del orden (asc/desc)") 
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        // El servicio usa el handle (nombre) para buscar en la API de PeerTube
        return channelService.getChannelFromPeerTube(handle, maxVideos, maxComments, name, page, size, sortBy, sortDir);
    }

    @Operation(
            summary = "Guardar canal en VideoMiner", 
            description = "Realiza la extracción del canal desde PeerTube y lo envía automáticamente al servidor central VideoMiner para su persistencia."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Canal procesado y enviado con éxito a VideoMiner"),
            @ApiResponse(responseCode = "404", description = "Canal no encontrado en PeerTube"),
            @ApiResponse(responseCode = "400", description = "Error de validación en los datos enviados a VideoMiner")
    })
    @PostMapping("/{handle}")
    @ResponseStatus(HttpStatus.CREATED)
    public Channel postChannel(
            @Parameter(description = "Nombre corto o 'handle' del canal") 
            @PathVariable String handle,
            
            @Parameter(description = "Máximo de vídeos a persistir") 
            @RequestParam(defaultValue = "10") @Min(1) Integer maxVideos,
            
            @Parameter(description = "Máximo de comentarios por vídeo") 
            @RequestParam(defaultValue = "2") @Min(0) Integer maxComments,
            
            @Parameter(description = "Filtrar por nombre antes de guardar") 
            @RequestParam(required = false) String name,
            
            @Parameter(description = "Página de vídeos a guardar") 
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            
            @Parameter(description = "Tamaño de la página a guardar") 
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            
            @Parameter(description = "Orden de los vídeos") 
            @RequestParam(defaultValue = "id") String sortBy,
            
            @Parameter(description = "Dirección del orden") 
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Channel channel = channelService.getChannelFromPeerTube(handle, maxVideos, maxComments, name, page, size, sortBy, sortDir);
        
        if (channel != null) {
            videoMinerService.saveChannel(channel);
        }
        
        return channel;
    }
}