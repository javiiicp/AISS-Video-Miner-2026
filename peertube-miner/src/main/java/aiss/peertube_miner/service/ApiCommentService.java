package aiss.peertube_miner.service;

import aiss.peertube_miner.model.external.ApiComment;
import aiss.peertube_miner.model.external.DataComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.ArrayList;

public class ApiCommentService {
    
    @Autowired
    RestTemplate restTemplate;

    private final String HOST = "https://peertube2.cpy.re";

    public List<DataComment> getComments(String videoId) {
        // 1. Construimos la URL según la documentación de PeerTube
        String url = HOST + "/api/v1/videos/" + videoId + "/comment-threads";

        // 2. Hacemos la llamada (Uso de getForObject como en la P3)
        // Recibimos el ApiComment (la "caja" que contiene el total y la lista data)
        ApiComment response = restTemplate.getForObject(url, ApiComment.class);

        // 3. Devolvemos la lista de comentarios (data)
        if (response != null && response.getData() != null) {
            return response.getData();
        } else {
            return new ArrayList<>(); // Devolvemos lista vacía si no hay nada
        }
    }
}
