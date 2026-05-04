package aiss.dailymotion_miner.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Video", description = "Represents a Dailymotion video with metadata, author, comments and captions")
public class Video {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the video", example = "video123")
    private String id;

    @JsonProperty("name")
    @Schema(description = "Title of the video", example = "Java Tutorials", required = true)
    private String name;

    @JsonProperty("description")
    @Schema(description = "Detailed description of the video content", example = "Learn Java programming basics")
    private String description;

    @JsonProperty("releaseTime")
    @Schema(description = "Timestamp when the video was released", example = "2024-01-15T14:30:00Z", required = true)
    private String releaseTime;

    @JsonProperty("author")
    @Schema(description = "User who created this video")
    private User author;

    @JsonProperty("comments")
    @Schema(description = "List of comments associated with this video")
    private List<Comment> comments;

    @JsonProperty("captions")
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