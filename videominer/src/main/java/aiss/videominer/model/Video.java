package aiss.videominer.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Video")
@Schema(description = "Detalles del vídeo almacenado")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("id")
    @Schema(description = "ID del vídeo (generado automáticamente)", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @JsonProperty("name")
    @NotEmpty(message = "El nombre del vídeo no puede estar vacío")
    @Schema(description = "Título del vídeo", example = "Tutorial de Spring Boot")
    private String name;

    @JsonProperty("description")
    @Column(columnDefinition="TEXT")
    @Schema(description = "Sinopsis del vídeo")
    private String description;

    @JsonProperty("releaseTime")
    @NotEmpty(message = "La fecha de publicación no puede estar vacía")
    @Schema(description = "Fecha en la que se publicó el vídeo", example = "2023-10-27T10:00:00Z")
    private String releaseTime;

    @JsonProperty("author")
    @ManyToOne(cascade = CascadeType.ALL)
    @Valid
    @NotNull(message = "El vídeo debe tener un autor")
    @Schema(description = "Autor del vídeo")
    private User author;

    @JsonProperty("comments")
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Comentarios recibidos", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Comment> comments;

    @JsonProperty("captions")
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Subtítulos disponibles", accessMode = Schema.AccessMode.READ_ONLY)
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
        return "Video{" + "id='" + id + '\'' + ", name='" + name + '\'' + '}';
    }
}