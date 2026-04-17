package aiss.peertube_miner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Clase de apoyo para leer la respuesta de la API de PeerTube.
 * PeerTube devuelve los comentarios dentro de una propiedad "data".
 */
public class CommentResponse {

    // Esta anotación es la que hace la magia de conectar el JSON con tu lista
    @JsonProperty("data")
    private List<Comment> data;

    public List<Comment> getData() {
        return data;
    }

    public void setData(List<Comment> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CommentResponse{" +
                "data=" + data +
                '}';
    }
}