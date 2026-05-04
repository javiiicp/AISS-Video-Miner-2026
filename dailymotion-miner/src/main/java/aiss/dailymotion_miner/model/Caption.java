package aiss.dailymotion_miner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Table(name = "Caption")
@Schema(name = "Caption", description = "Represents a caption or subtitle for a video")
public class Caption {

    @Id
    @JsonProperty("id")
    @Schema(description = "Unique identifier of the caption", example = "caption123")
    private String id;

    @JsonProperty("link")
    @Schema(description = "URL link to the caption file", example = "https://example.com/captions/en.vtt")
    private String link;

    @JsonProperty("language")
    @Schema(description = "Language code of the caption", example = "en", allowableValues = {"en", "es", "fr", "de"})
    private String language;

    @ManyToOne
    @JoinColumn(name = "videoId")
    @NotNull(message = "Caption must be associated with a video")
    @Schema(description = "The video this caption belongs to")
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
        return "Caption{" +
                "id='" + id + '\'' +
                ", link='" + link + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
