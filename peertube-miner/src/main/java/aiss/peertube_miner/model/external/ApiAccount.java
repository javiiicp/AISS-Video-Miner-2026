package aiss.peertube_miner.model.external;

import java.util.List;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"url",
"name",
"host",
"avatars",
"id",
"displayName"
})
@Generated("jsonschema2pojo")
@Schema(description = "Detalles de la cuenta del autor en PeerTube")
public class ApiAccount {

    @JsonProperty("url")
    @Schema(description = "Enlace al perfil del autor")
    private String url;

    @JsonProperty("name")
    @Schema(description = "Nombre de usuario único", example = "juan_pt")
    private String name;

    @JsonProperty("host")
    private String host;

    @JsonProperty("avatars")
    @Schema(description = "Lista de imágenes de perfil (avatares)")
    private List<ApiAvatar> avatars;

    @JsonProperty("id")
    @Schema(description = "ID interno de la cuenta")
    private Integer id;

    @JsonProperty("displayName")
    @Schema(description = "Nombre mostrado públicamente", example = "Juan")
    private String displayName;

    @JsonProperty("url")
    public String getUrl() { return url; }
    @JsonProperty("url")
    public void setUrl(String url) { this.url = url; }
    @JsonProperty("name")
    public String getName() { return name; }
    @JsonProperty("name")
    public void setName(String name) { this.name = name; }
    @JsonProperty("host")
    public String getHost() { return host; }
    @JsonProperty("host")
    public void setHost(String host) { this.host = host; }
    @JsonProperty("avatars")
    public List<ApiAvatar> getAvatars() { return avatars; }
    @JsonProperty("avatars")
    public void setAvatars(List<ApiAvatar> avatars) { this.avatars = avatars; }
    @JsonProperty("id")
    public Integer getId() { return id; }
    @JsonProperty("id")
    public void setId(Integer id) { this.id = id; }
    @JsonProperty("displayName")
    public String getDisplayName() { return displayName; }
    @JsonProperty("displayName")
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}