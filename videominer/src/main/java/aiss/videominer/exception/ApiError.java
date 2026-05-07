package aiss.videominer.exception;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estructura de error estándar de la API")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    @Schema(description = "Fecha y hora del error", example = "2023-10-27T10:00:00Z")
    private String timestamp;
    
    @Schema(description = "Código de estado HTTP", example = "400")
    private int status;
    
    @Schema(description = "Nombre del error", example = "Bad Request")
    private String error;
    
    @Schema(description = "Mensaje detallado", example = "El nombre no puede estar vacío")
    private String message;
    
    @Schema(description = "Errores específicos de cada campo (solo en validaciones 400)")
    private Map<String, String> fieldErrors;

    public ApiError(String timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public String getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public Map<String, String> getFieldErrors() { return fieldErrors; }
    public void setFieldErrors(Map<String, String> fieldErrors) { this.fieldErrors = fieldErrors; }
}