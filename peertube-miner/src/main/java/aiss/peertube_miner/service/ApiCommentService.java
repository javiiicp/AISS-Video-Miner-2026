package aiss.peertube_miner.service;

import aiss.peertube_miner.model.external.ApiComment;
import aiss.peertube_miner.model.external.DataComment;
import aiss.peertube_miner.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class ApiCommentService {
    
    @Autowired
    RestTemplate restTemplate;

    private final String HOST = "https://peertube2.cpy.re";

    public List<
    Comment> getCommentsForASpecificVideo(String videoId, Integer maxComments) {

        // 1. Construimos la URL según la documentación de PeerTube
        String url = HOST + "/api/v1/videos/" + videoId + "/comment-threads?count=" + maxComments;

        // 2. Hacemos la llamada (Uso de getForObject como en la P3)
        // Recibimos el ApiComment (la "caja" que contiene el total y la lista data)
        ApiComment response = restTemplate.getForObject(url, ApiComment.class);

        // 3. Devolvemos la lista de comentarios (data)
        List<DataComment> comments = (response != null && response.getData() != null) ? response.getData() : new ArrayList<>();
        
        return comments.stream().map(this::transformToComment).collect(Collectors.toList());
    }

    public Comment transformToComment(DataComment dataComment) {
        Comment comment = new Comment();

        // 1. Mapear el ID (PeerTube da un Integer, tú necesitas un String)
        if (dataComment.getId() != null) {
            comment.setId(dataComment.getId().toString());
        }

        // 2. Mapear el Texto
        comment.setText(dataComment.getText());

        // 3. Mapear la Fecha de Creación (PeerTube lo da como createdAt, tú lo quieres como createdOn)
        comment.setCreatedOn(dataComment.getCreatedAt());

        return comment;
    }
}
