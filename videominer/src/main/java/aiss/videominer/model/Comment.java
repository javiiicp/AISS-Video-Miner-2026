package aiss.videominer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
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
@Table(name = "Comment")
@Schema(description = "Comentario de un usuario en un vídeo")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("id")
    @Schema(description = "ID del comentario (generado automáticamente)", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @JsonProperty("text")
    @Column(columnDefinition="TEXT")
    @Schema(description = "Contenido del comentario", example = "¡Buen vídeo!")
    private String text;

    @JsonProperty("createdOn")
    @Schema(description = "Fecha de creación del comentario", example = "2023-10-27T11:00:00Z")
    private String createdOn;

    @ManyToOne()
    @JoinColumn(name = "videoId")
    @NotNull(message = "El comentario debe estar asociado a un vídeo")
    @JsonIgnoreProperties({"comments", "captions"})
    @Schema(description = "Vídeo al que pertenece el comentario")
    private Video video;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    @Override
    public String toString() {
        return "Comment{" + "id='" + id + '\'' + ", text='" + text + '\'' + '}';
    }
}