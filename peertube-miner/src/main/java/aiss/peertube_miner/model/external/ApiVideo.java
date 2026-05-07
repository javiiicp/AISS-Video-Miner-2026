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
@Schema(description = "Contenedor de la lista de vídeos devuelta por PeerTube")
public class ApiVideo {

    @JsonProperty("total")
    @Schema(description = "Número total de vídeos que tiene el canal en PeerTube", example = "150")
    private Integer total;

    @JsonProperty("data")
    @Schema(description = "Lista de vídeos de la página actual")
    private List<DataVideo> data;

    @JsonProperty("total")
    public Integer getTotal() { return total; }
    @JsonProperty("total")
    public void setTotal(Integer total) { this.total = total; }
    @JsonProperty("data")
    public List<DataVideo> getData() { return data; }
    @JsonProperty("data")
    public void setData(List<DataVideo> data) { this.data = data; }
}