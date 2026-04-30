package aiss.videominer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Comment {

    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("text")
    @Column(columnDefinition="TEXT")
    private String text;

    @JsonProperty("createdOn")
    private String createdOn;

    @ManyToOne() // Un comentario pertenece a un vídeo
    @JoinColumn(name = "videoId") // Columna de unión en la BD
    @NotNull(message = "El comentario debe estar asociado a un vídeo")
    @JsonIgnoreProperties({"comments", "captions"})
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
        return "Comment{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", createdOn='" + createdOn + '\'' +
                '}';
    }
}
