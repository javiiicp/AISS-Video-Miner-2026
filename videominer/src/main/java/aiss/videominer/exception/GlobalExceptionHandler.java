package aiss.videominer.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
        ApiError apiError = new ApiError(
            Instant.now().toString(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Error de validación en los campos del JSON"
        );
        
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        apiError.setFieldErrors(fieldErrors);
        
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatusCode statusCode = ex.getStatusCode();
        HttpStatus status = HttpStatus.valueOf(statusCode.value());
        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();

        ApiError apiError = new ApiError(Instant.now().toString(), status.value(), status.getReasonPhrase(), message);
        return ResponseEntity.status(status).body(apiError);
    }

    @ExceptionHandler({
        UserNotFoundException.class, 
        VideoNotFoundException.class, 
        CaptionNotFoundException.class, 
        ChannelNotFoundException.class, 
        CommentNotFoundException.class
    })
    public ResponseEntity<ApiError> handleNotFoundExceptions(RuntimeException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = ex.getMessage() != null ? ex.getMessage() : status.getReasonPhrase();
        
        ApiError apiError = new ApiError(Instant.now().toString(), status.value(), status.getReasonPhrase(), message);
        return ResponseEntity.status(status).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(Exception ex) {
        logger.error("Unexpected server error", ex);
        ApiError apiError = new ApiError(
            Instant.now().toString(), 
            HttpStatus.INTERNAL_SERVER_ERROR.value(), 
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), 
            "Error inesperado del servidor"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }
}