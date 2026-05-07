package aiss.peertube_miner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Comentario de un vídeo")
public class Comment {

    @JsonProperty("id")
    @Schema(description = "ID único del comentario")
    private String id;

    @JsonProperty("text")
    @Schema(description = "Contenido textual del comentario")
    private String text;

    @JsonProperty("createdOn")
    @Schema(description = "Fecha de publicación del comentario", example = "2024-05-22T09:30:00Z")
    private String createdOn;

    public Comment() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getCreatedOn() { return createdOn; }
    public void setCreatedOn(String createdOn) { this.createdOn = createdOn; }
}