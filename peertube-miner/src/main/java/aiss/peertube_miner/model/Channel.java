package aiss.peertube_miner.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos del canal de PeerTube recolectados")
public class Channel {

    @JsonProperty("id")
    @Schema(description = "ID interno del canal (proporcionado por PeerTube)", example = "123")
    private String id;

    @JsonProperty("name")
    @Schema(description = "Nombre corto o 'handle' del canal utilizado para las búsquedas", example = "aiss_channel")
    private String name;

    @JsonProperty("description")
    @Schema(description = "Descripción biográfica del canal")
    private String description;

    @JsonProperty("createdTime")
    @Schema(description = "Fecha de creación del canal en formato ISO 8601", example = "2024-05-20T10:00:00Z")
    private String createdTime;

    @JsonProperty("videos")
    @Schema(description = "Lista de vídeos pertenecientes a este canal")
    private List<Video> videos;

    public Channel() {
        this.videos = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}