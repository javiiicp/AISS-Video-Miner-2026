package aiss.peertube_miner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Transforma la excepción en un 404 REST
public class ChannelNotFoundException extends RuntimeException {
    public ChannelNotFoundException() {
        super("El canal no existe en PeerTube");
    }
}