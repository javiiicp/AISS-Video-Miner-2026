package aiss.peertube_miner.model.external;

import java.util.List;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "total",
    "data"
})
@Generated("jsonschema2pojo")
@Schema(description = "Contenedor de hilos de comentarios de un vídeo")
public class ApiComment {

    @JsonProperty("total")
    @Schema(description = "Número total de comentarios")
    private Integer total;

    @JsonProperty("data")
    @Schema(description = "Lista de comentarios extraídos")
    private List<DataComment> data;

    @JsonProperty("total")
    public Integer getTotal() { return total; }
    @JsonProperty("total")
    public void setTotal(Integer total) { this.total = total; }
    @JsonProperty("data")
    public List<DataComment> getData() { return data; }
    @JsonProperty("data")
    public void setData(List<DataComment> data) { this.data = data; }
}