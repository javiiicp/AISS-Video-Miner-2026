package aiss.peertube_miner.model.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Datos de un archivo de subtítulos de PeerTube")
public class DataCaption {
    @JsonProperty("id")
    @Schema(description = "Identificador del subtítulo")
    private String id;
    @JsonProperty("language")
    @Schema(description = "Idioma (ej: es, en)")
    private String language;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }
}