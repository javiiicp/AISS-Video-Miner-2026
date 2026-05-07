package aiss.peertube_miner.model.external;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"id",
"url",
"name",
"createdAt",
"updatedAt",
"displayName",
"description"
})
@Generated("jsonschema2pojo")
@Schema(description = "Respuesta bruta de un canal desde la API de PeerTube")
public class ApiChannel {

    @JsonProperty("id")
    @Schema(description = "ID numérico interno de PeerTube", example = "1")
    private Integer id;

    @JsonProperty("url")
    @Schema(description = "URL completa del canal en la instancia de PeerTube")
    private String url;

    @JsonProperty("name")
    @Schema(description = "Nombre único (handle) del canal, usado para las búsquedas", example = "aiss_channel")
    private String name;

    @JsonProperty("createdAt")
    @Schema(description = "Fecha de creación del canal", example = "2024-01-01T10:00:00Z")
    private String createdAt;

    @JsonProperty("updatedAt")
    @Schema(description = "Fecha de última actualización del canal", example = "2024-01-01T10:00:00Z")
    private String updatedAt;

    @JsonProperty("displayName")
    @Schema(description = "Nombre que se muestra públicamente", example = "AISS Project Channel")
    private String displayName;

    @JsonProperty("description")
    @Schema(description = "Descripción o biografía del canal")
    private String description;

    @JsonProperty("id")
    public Integer getId() { return id; }
    @JsonProperty("id")
    public void setId(Integer id) { this.id = id; }
    @JsonProperty("url")
    public String getUrl() { return url; }
    @JsonProperty("url")
    public void setUrl(String url) { this.url = url; }
    @JsonProperty("name")
    public String getName() { return name; }
    @JsonProperty("name")
    public void setName(String name) { this.name = name; }
    @JsonProperty("createdAt")
    public String getCreatedAt() { return createdAt; }
    @JsonProperty("createdAt")
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    @JsonProperty("updatedAt")
    public String getUpdatedAt() { return updatedAt; }
    @JsonProperty("updatedAt")
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    @JsonProperty("displayName")
    public String getDisplayName() { return displayName; }
    @JsonProperty("displayName")
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    @JsonProperty("description")
    public String getDescription() { return description; }
    @JsonProperty("description")
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "ApiChannel [id=" + id + ", name=" + name + ", displayName=" + displayName + "]";
    }
}