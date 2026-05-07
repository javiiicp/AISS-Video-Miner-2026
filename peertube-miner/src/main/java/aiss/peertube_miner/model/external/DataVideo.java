package aiss.peertube_miner.model.external;

import java.util.List;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"id",
"publishedAt",
"truncatedDescription",
"name",
"account"
})
@Generated("jsonschema2pojo")
@Schema(description = "Datos brutos de un vídeo extraído de PeerTube")
public class DataVideo {

    @JsonProperty("id")
    @Schema(description = "ID técnico del vídeo en la base de datos de PeerTube", example = "45")
    private Integer id;

    @JsonProperty("publishedAt")
    @Schema(description = "Fecha de publicación del vídeo", example = "2024-05-20T12:00:00Z")
    private String publishedAt;

    @JsonProperty("truncatedDescription")
    @Schema(description = "Descripción corta o resumida del vídeo")
    private String truncatedDescription;

    @JsonProperty("name")
    @Schema(description = "Título del vídeo")
    private String name;

    @JsonProperty("account")
    @Schema(description = "Información de la cuenta del autor")
    private ApiAccount account;

    @JsonProperty("captions")
    private List<Object> captions;

    @JsonProperty("id")
    public Integer getId() { return id; }
    @JsonProperty("id")
    public void setId(Integer id) { this.id = id; }
    @JsonProperty("publishedAt")
    public String getPublishedAt() { return publishedAt; }
    @JsonProperty("publishedAt")
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
    @JsonProperty("truncatedDescription")
    public String getTruncatedDescription() { return truncatedDescription; }
    @JsonProperty("truncatedDescription")
    public void setTruncatedDescription(String truncatedDescription) { this.truncatedDescription = truncatedDescription; }
    @JsonProperty("name")
    public String getName() { return name; }
    @JsonProperty("name")
    public void setName(String name) { this.name = name; }
    @JsonProperty("account")
    public ApiAccount getAccount() { return account; }
    @JsonProperty("account")
    public void setAccount(ApiAccount account) { this.account = account; }
    @JsonProperty("captions")
    public List<Object> getCaptions() { return captions; }
    @JsonProperty("captions")
    public void setCaptions(List<Object> captions) { this.captions = captions; }

    @Override
    public String toString() {
        return "DataVideo [id=" + id + ", name=" + name + "]";
    }
}