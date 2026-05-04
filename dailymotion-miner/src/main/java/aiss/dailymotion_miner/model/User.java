package aiss.dailymotion_miner.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "User", description = "Represents a Dailymotion user/channel owner")
public class User {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the user", example = "user123")
    private String id;

    @JsonProperty("name")
    @Schema(description = "Full name of the user", example = "John Doe", required = true)
    private String name;

    @JsonProperty("user_link")
    @Schema(description = "URL link to the user's profile", example = "https://www.dailymotion.com/user123")
    private String user_link;

    @JsonProperty("picture_link")
    @Schema(description = "URL link to the user's profile picture", example = "https://example.com/pictures/user123.jpg")
    private String picture_link;

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

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", user_link='" + user_link + '\'' +
                ", picture_link='" + picture_link + '\'' +
                '}';
    }
}