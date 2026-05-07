package aiss.peertube_miner.model.external;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiCaption {

    @JsonProperty("total")
    private Integer total;
    
    @JsonProperty("data")
    private List<DataCaption> data;

    @JsonProperty("total")
    public Integer getTotal() {
        return total;
    }

    @JsonProperty("total")
    public void setTotal(Integer total) {
        this.total = total;
    }

    @JsonProperty("data")
    public List<DataCaption> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<DataCaption> data) {
        this.data = data;
    }
}