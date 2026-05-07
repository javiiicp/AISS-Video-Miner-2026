package aiss.peertube_miner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información del autor en PeerTube")
public class User {

    @JsonProperty("id")
    @Schema(description = "ID del usuario", example = "10")
    private String id;

    @JsonProperty("name")
    @Schema(description = "Nombre visible del autor", example = "AISS User")
    private String name;

    @JsonProperty("user_link")
    @Schema(description = "URL del perfil del autor en la plataforma")
    private String user_link;

    @JsonProperty("picture_link")
    @Schema(description = "URL del avatar del autor")
    private String picture_link;

    public User() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUser_link() { return user_link; }
    public void setUser_link(String user_link) { this.user_link = user_link; }
    public String getPicture_link() { return picture_link; }
    public void setPicture_link(String picture_link) { this.picture_link = picture_link; }
}