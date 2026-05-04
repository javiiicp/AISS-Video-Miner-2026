package aiss.dailymotion_miner.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

/**
 * @author Juan C. Alonso
 */
@Entity
@Table(name = "Video")
@Schema(name = "Video", description = "Represents a Dailymotion video with metadata, author, comments and captions")
public class Video {

    @Id
    @JsonProperty("id")
    @Schema(description = "Unique identifier of the video", example = "video123")
    private String id;

    @JsonProperty("name")
    @NotEmpty(message = "Video name cannot be empty")
    @Schema(description = "Title of the video", example = "Java Tutorials", required = true)
    private String name;

    @JsonProperty("description")
    @Column(columnDefinition="TEXT")
    @Schema(description = "Detailed description of the video content", example = "Learn Java programming basics")
    private String description;

    @JsonProperty("releaseTime")
    @NotEmpty(message = "Video release time cannot be empty")
    @Schema(description = "Timestamp when the video was released", example = "2024-01-15T14:30:00Z", required = true)
    private String releaseTime;

    @JsonProperty("author")
    @OneToOne(cascade = CascadeType.ALL)
    @Schema(description = "User who created this video")
    private User author;

    @JsonProperty("comments")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "videoId")
    @Schema(description = "List of comments associated with this video")
    private List<Comment> comments;

    @JsonProperty("captions")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "videoId")
    @Schema(description = "List of captions/subtitles for this video")
    private List<Caption> captions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
    
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Caption> getCaptions() {
        return captions;
    }

    public void setCaptions(List<Caption> captions) {
        this.captions = captions;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", releaseTime='" + releaseTime + '\'' +
                ", author=" + author +
                ", comments=" + comments +
                ", captions=" + captions +
                '}';
    }
}
