package aiss.peertube_miner.mapper;

import java.util.ArrayList;
import aiss.peertube_miner.model.*;
import aiss.peertube_miner.model.external.*;

/**
 * Clase encargada de transformar los objetos de la API externa de PeerTube
 * a los modelos internos que entiende VideoMiner.
 */
public class PeertubeMapper {

    public static Channel toChannel(ApiChannel external) {
        if (external == null) return null;
        Channel channel = new Channel();
        
        // El ID externo se usa temporalmente en el minero
        channel.setId(external.getId() != null ? external.getId().toString() : null);
        channel.setName(external.getName());
        channel.setDescription(external.getDescription());
        channel.setCreatedTime(external.getCreatedAt());
        channel.setVideos(new ArrayList<>());
        
        return channel;
    }

    public static Video toVideo(DataVideo external) {
        if (external == null) return null;
        Video video = new Video();
        
        // Guardamos el ID de PeerTube para que el servicio pueda buscar comentarios/captions
        video.setId(external.getId() != null ? external.getId().toString() : null);
        video.setName(external.getName()); 
        video.setDescription(external.getTruncatedDescription());
        video.setReleaseTime(external.getPublishedAt());
        
        video.setComments(new ArrayList<>()); 
        video.setCaptions(new ArrayList<>());
        
        return video;
    }

    public static User toUser(ApiAccount external) {
        if (external == null) return null;
        User user = new User();
        
        user.setId(external.getId() != null ? external.getId().toString() : null);
        user.setName(external.getDisplayName()); // PeerTube usa displayName para el nombre público
        user.setUser_link(external.getUrl());
        
        // Extraemos la primera imagen de la lista de avatars si existe
        if (external.getAvatars() != null && !external.getAvatars().isEmpty()) {
            user.setPicture_link(external.getAvatars().get(0).getFileUrl());
        }
        
        return user;
    }

    public static Caption toCaption(DataCaption external) {
        if (external == null) return null;
        Caption caption = new Caption();
        
        caption.setId(external.getId());
        caption.setLanguage(external.getLanguage());
        // Generamos el enlace al subtítulo según la estructura de PeerTube
        caption.setLink("https://peertube.tv/api/v1/videos/captions/" + external.getId());
        
        return caption;
    }

    public static Comment toComment(DataComment external) {
        if (external == null) return null;
        Comment comment = new Comment();
        
        comment.setId(external.getId() != null ? external.getId().toString() : null);
        comment.setText(external.getText());
        comment.setCreatedOn(external.getCreatedAt());
        
        return comment;
    }
}