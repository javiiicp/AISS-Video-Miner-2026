package aiss.peertube_miner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Subtítulo disponible para un vídeo")
public class Caption {

    @JsonProperty("id")
    @Schema(description = "Identificador del subtítulo")
    private String id;

    @JsonProperty("link")
    @Schema(description = "Enlace directo al archivo de subtítulos (VTT/SRT)", example = "https://peertube.tv/captions/es.vtt")
    private String link;

    @JsonProperty("language")
    @Schema(description = "Código del idioma", example = "es")
    private String language;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}