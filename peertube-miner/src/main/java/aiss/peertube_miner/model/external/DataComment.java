package aiss.peertube_miner.model.external;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "text",
    "createdAt"
})
@Generated("jsonschema2pojo")
@Schema(description = "Información de un comentario de PeerTube")
public class DataComment {
    
    @JsonProperty("id")
    @Schema(description = "ID interno del comentario")
    private Integer id;

    @JsonProperty("text")
    @Schema(description = "Texto del comentario")
    private String text;

    @JsonProperty("createdAt")
    @Schema(description = "Fecha de publicación", example = "2024-05-22T09:30:00Z")
    private String createdAt;

    @JsonProperty("id")
    public Integer getId() { return id; }
    @JsonProperty("id")
    public void setId(Integer id) { this.id = id; }
    @JsonProperty("text")
    public String getText() { return text; }
    @JsonProperty("text")
    public void setText(String text) { this.text = text; }
    @JsonProperty("createdAt")
    public String getCreatedAt() { return createdAt; }
    @JsonProperty("createdAt")
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}