package aiss.dailymotion_miner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(name = "Comment", description = "Represents a user comment on a video")
public class Comment {

    @Id
    @JsonProperty("id")
    @Schema(description = "Unique identifier of the comment", example = "comment123")
    private String id;

    @JsonProperty("text")
    @Column(columnDefinition="TEXT")
    @Schema(description = "Content of the comment", example = "Great video, very informative!")
    private String text;

    @JsonProperty("createdOn")
    @Schema(description = "Timestamp when the comment was created", example = "2024-01-15T15:20:00Z")
    private String createdOn;

    @ManyToOne()
    @JoinColumn(name = "videoId")
    @NotNull(message = "El comentario debe estar asociado a un vídeo")
    @Schema(description = "The video this comment belongs to")
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

    public void setCreatedOn(String videoDate) {
        this.createdOn = videoDate;
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
