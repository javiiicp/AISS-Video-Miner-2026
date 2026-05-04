package aiss.dailymotion_miner.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Channel", description = "Represents a Dailymotion channel with videos and metadata")
public class Channel {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the channel", example = "channel123")
    private String id;

    @JsonProperty("name")
    @Schema(description = "Name of the channel", example = "My Channel", required = true)
    private String name;

    @JsonProperty("description")
    @Schema(description = "Detailed description of the channel", example = "Channel about technology and tutorials")
    private String description;

    @JsonProperty("createdTime")
    @Schema(description = "Timestamp when the channel was created", example = "2024-01-15T10:30:00Z", required = true)
    private String createdTime;

    @JsonProperty("videos")
    @Schema(description = "List of videos associated with this channel", required = true)
    private List<Video> videos;

    public Channel() {
        this.videos = new ArrayList<>();
    }

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

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdTime='" + createdTime + '\'' +
                ", videos=" + videos +
                '}';
    }
}