package aiss.peertube_miner.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalles de un vídeo de PeerTube")
public class Video {

    @JsonProperty("id")
    @Schema(description = "UUID o ID identificador del vídeo", example = "uuid-1234-5678")
    private String id;

    @JsonProperty("name")
    @Schema(description = "Título del vídeo", example = "Tutorial de Microservicios")
    private String name;

    @JsonProperty("description")
    @Schema(description = "Sinopsis o descripción del contenido")
    private String description;

    @JsonProperty("releaseTime")
    @Schema(description = "Fecha de publicación del vídeo", example = "2024-05-21T12:00:00Z")
    private String releaseTime;

    @JsonProperty("user")
    @Schema(description = "Información del autor del vídeo")
    private User author;

    @JsonProperty("comments")
    @Schema(description = "Hilos de comentarios asociados al vídeo")
    private List<Comment> comments;

    @JsonProperty("captions")
    @Schema(description = "Subtítulos disponibles para el vídeo")
    private List<Caption> captions;

    public Video() {}

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

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
    
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Caption> getCaptions() {
        return captions;
    }

    public void setCaptions(List<Caption> captions) {
        this.captions = captions;
    }
}