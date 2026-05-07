package aiss.peertube_miner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import aiss.peertube_miner.model.Channel;
import aiss.peertube_miner.service.ApiChannelService;
import aiss.peertube_miner.service.VideoMinerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/channels")
@Tag(name = "1. Extracción de PeerTube", description = "Controlador de minería encargado de la extracción, transformación y carga (ETL) de datos desde la red PeerTube.")
@Validated
public class ChannelController {

    @Autowired
    private ApiChannelService channelService;

    @Autowired
    private VideoMinerService videoMinerService; 

    @Operation(
        summary = "Previsualizar Canal desde PeerTube", 
        description = """
                      ### Gu\u00eda de Extracci\u00f3n:
                      Este endpoint permite consultar un canal directamente desde la API externa de PeerTube sin guardarlo. Es ideal para validar el contenido antes de la persistencia.
                      
                      1. **L\u00edmites de Miner\u00eda**: Controla cu\u00e1ntos recursos extraer con `maxVideos` y `maxComments`.
                      2. **Filtrado y Paginaci\u00f3n**: Los par\u00e1metros `name`, `page` y `size` se aplican sobre la lista de v\u00eddeos del canal.
                      3. **Ordenaci\u00f3n**: Organiza los v\u00eddeos por fecha o ID mediante `sortBy` y `sortDir`.""")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Canal extraído y transformado correctamente.", 
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = Channel.class))),
            @ApiResponse(responseCode = "404", description = "El canal no existe en la instancia de PeerTube especificada.")
    })
    @GetMapping("/{id}")
    public Channel getChannel(
            @Parameter(description = "ID del canal en PeerTube (ej. 'stux'). NOTA: La API de Peertube utiliza el nombre del canal como identificador.", required = true, example = "stux") 
            @PathVariable String id, 
            @Parameter(description = "Número máximo de vídeos a extraer.", example = "10") 
            @RequestParam(defaultValue = "10") @Min(1) Integer maxVideos,
            @Parameter(description = "Número máximo de comentarios por vídeo a extraer.", example = "2") 
            @RequestParam(defaultValue = "2") @Min(0) Integer maxComments,
            @Parameter(description = "Filtrar vídeos del canal por nombre.", example = "Alien") 
            @RequestParam(required = false) String name,
            @Parameter(description = "Índice de página para los vídeos.", example = "0") 
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @Parameter(description = "Cantidad de vídeos por página.", example = "10") 
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "Campo de ordenación de vídeos.", example = "publishedAt") 
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Dirección de la ordenación.", example = "asc") 
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        return channelService.getChannelFromPeerTube(id, maxVideos, maxComments, name, page, size, sortBy, sortDir);
    }

    @Operation(
        summary = "Minar y Persistir Canal", 
        description = """
                      ### Proceso de Integraci\u00f3n:
                      Realiza la extracci\u00f3n completa desde PeerTube y **env\u00eda autom\u00e1ticamente** el objeto transformado a la API de VideoMiner para su almacenamiento persistente.
                      
                      **Nota t\u00e9cnica**: Este m\u00e9todo garantiza que los autores, comentarios y subt\u00edtulos se vinculen correctamente antes del env\u00edo.""")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Canal minado y guardado exitosamente en VideoMiner."),
            @ApiResponse(responseCode = "404", description = "No se pudo localizar el canal para iniciar el proceso de minería.")
    })
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Channel postChannel(
            @Parameter(description = "ID del canal a minar. NOTA: La API de Peertube utiliza el nombre del canal como identificador.", required = true, example = "stux") @PathVariable String id,
            @Parameter(description = "Límite de vídeos.", example = "5") @RequestParam(defaultValue = "10") @Min(1) Integer maxVideos,
            @Parameter(description = "Límite de comentarios.", example = "5") @RequestParam(defaultValue = "2") @Min(0) Integer maxComments,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        // 1. Extracción y Transformación (ET)
        Channel channel = channelService.getChannelFromPeerTube(id, maxVideos, maxComments, name, page, size, sortBy, sortDir);
        
        // 2. Carga en VideoMiner (L)
        if (channel != null) {
            videoMinerService.saveChannel(channel);
        }
        
        return channel;
    }
}