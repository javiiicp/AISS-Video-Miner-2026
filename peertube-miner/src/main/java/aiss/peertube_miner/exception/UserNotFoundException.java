package aiss.peertube_miner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Esto hace la magia del 404
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("El usuario no existe en PeerTube");
    }
}