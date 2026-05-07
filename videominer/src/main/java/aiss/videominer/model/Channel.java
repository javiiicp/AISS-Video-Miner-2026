package aiss.videominer.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Channel")
@Schema(description = "Representa un canal de una plataforma de vídeo")
public class Channel {

    @Id
    @JsonProperty("id")
    @NotEmpty(message = "El id del canal no puede estar vacío")
    @Schema(description = "ID del canal (generado automáticamente)", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @JsonProperty("name")
    @NotEmpty(message = "El nombre del canal no puede estar vacío")
    @Schema(description = "Nombre del canal", example = "Mi Canal de Tecnología")
    private String name;

    @JsonProperty("description")
    @Column(columnDefinition="TEXT")
    @Schema(description = "Descripción del canal", example = "Un canal sobre reviews...")
    private String description;

    @JsonProperty("createdTime")
    @NotEmpty(message = "La fecha de creación del canal no puede estar vacía")
    @Schema(description = "Fecha de creación del canal", example = "2023-10-27T10:00:00Z")    
    private String createdTime;

    @JsonProperty("videos")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "channelId")
    @Valid
    @NotNull(message = "La lista de vídeos del canal no puede ser nula")
    @Schema(description = "Lista de vídeos asociados al canal")
    private List<Video> videos;

    public Channel() { this.videos = new ArrayList<>(); }

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
        return "Channel{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", videos=" + videos + '}';
    }
}