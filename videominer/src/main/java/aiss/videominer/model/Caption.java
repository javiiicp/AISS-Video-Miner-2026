package aiss.videominer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * @author Juan C. Alonso
 */
@Entity
@Table(name = "Caption")
@Schema(description = "Subtítulos de un vídeo")
public class Caption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("id")
    @Schema(description = "ID del subtítulo (generado automáticamente)", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @JsonProperty("link")
    @Schema(description = "Enlace al archivo de subtítulos", example = "https://example.com/subs.vtt")
    private String link;

    @JsonProperty("language")
    @Schema(description = "Idioma de los subtítulos", example = "es")
    private String language;

    @ManyToOne
    @JoinColumn(name = "videoId")
    @NotNull(message = "El subtítulo debe estar asociado a un vídeo")
    @JsonIgnoreProperties({"comments", "captions"})
    @Schema(description = "Vídeo asociado")
    private Video video;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    @Override
    public String toString() {
        return "Caption{" + "id='" + id + '\'' + ", language='" + language + '\'' + '}';
    }
}