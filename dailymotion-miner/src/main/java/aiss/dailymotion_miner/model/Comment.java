package aiss.dailymotion_miner.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Comment", description = "Represents a user comment on a video")
public class Comment {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the comment", example = "comment123")
    private String id;

    @JsonProperty("text")
    @Schema(description = "Content of the comment", example = "Great video, very informative!")
    private String text;

    @JsonProperty("createdOn")
    @Schema(description = "Timestamp when the comment was created", example = "2024-01-15T15:20:00Z")
    private String createdOn;

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

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", createdOn='" + createdOn + '\'' +
                '}';
    }
}