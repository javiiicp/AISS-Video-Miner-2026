package aiss.videominer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Video")
public class Video {

    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    @NotEmpty(message = "El nombre del vídeo no puede estar vacío")
    private String name;

    @JsonProperty("description")
    @Column(columnDefinition="TEXT")
    private String description;

    @JsonProperty("releaseTime")
    @NotEmpty(message = "La fecha de publicación no puede estar vacía")
    private String releaseTime;

    @JsonProperty("author")
    @JsonAlias("user")
    @ManyToOne(cascade = CascadeType.ALL)
    @Valid
    @NotNull(message = "El vídeo debe tener un autor")
    private User author;

    @JsonProperty("comments")
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @JsonProperty("captions")
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Caption> captions = new ArrayList<>();

    @PrePersist
    public void generateId() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
    }

    public void setComments(List<Comment> comments) { 
        this.comments = comments; 
        if (comments != null) {
            comments.forEach(c -> c.setVideo(this));
        }
    }

    public void setCaptions(List<Caption> captions) { 
        this.captions = captions; 
        if (captions != null) {
            captions.forEach(c -> c.setVideo(this));
        }
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

    public List<Caption> getCaptions() {
        return captions;
    }

    @Override
    public String toString() {
        return "Video [id=" + id + ", name=" + name + ", description=" + description + ", releaseTime=" + releaseTime
                + ", author=" + author + ", comments=" + comments + ", captions=" + captions + "]";
    }

}