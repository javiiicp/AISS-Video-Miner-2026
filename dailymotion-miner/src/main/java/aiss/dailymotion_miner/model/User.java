package aiss.dailymotion_miner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

/**
 * @author Juan C. Alonso
 */
@Entity
@Table(name = "VMUser")
@Schema(name = "User", description = "Represents a Dailymotion user/channel owner")
public class User {

    @Id
    @JsonProperty("id")
    @Schema(description = "Unique identifier of the user", example = "user123")
    private String id;

    @JsonProperty("name")
    @NotEmpty(message = "El nombre del usuario no puede estar vacío")
    @Schema(description = "Full name of the user", example = "John Doe", required = true)
    private String name;

    @JsonProperty("user_link")
    @Schema(description = "URL link to the user's profile", example = "https://www.dailymotion.com/user123")
    private String user_link;

    @JsonProperty("picture_link")
    @Schema(description = "URL link to the user's profile picture", example = "https://example.com/pictures/user123.jpg")
    private String picture_link;

    @JsonIgnore
    @OneToOne(mappedBy = "author")
    @Schema(description = "Video created by this user")
    private Video video;

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

    public String getUser_link() {
        return user_link;
    }

    public void setUser_link(String user_link) {
        this.user_link = user_link;
    }

    public String getPicture_link() {
        return picture_link;
    }

    public void setPicture_link(String picture_link) {
        this.picture_link = picture_link;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", user_link='" + user_link + '\'' +
                ", picture_link='" + picture_link + '\'' +
                ", video=" + video +
                '}';
    }

}
