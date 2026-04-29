package aiss.peertube_miner.model.external;

import java.util.List;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"total",
"data"
})
@Generated("jsonschema2pojo")
public class ApiChannel {

@JsonProperty("total")
private Integer total;
@JsonProperty("data")
private List<DataChannel> data;

@JsonProperty("total")
public Integer getTotal() {
return total;
}

@JsonProperty("total")
public void setTotal(Integer total) {
this.total = total;
}

@JsonProperty("data")
public List<DataChannel> getData() {
return data;
}

@JsonProperty("data")
public void setData(List<DataChannel> data) {
this.data = data;
}

@Override
public String toString() {
StringBuilder sb = new StringBuilder();
sb.append(ApiChannel.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
sb.append("total");
sb.append('=');
sb.append(((this.total == null)?"<null>":this.total));
sb.append(',');
sb.append("data");
sb.append('=');
sb.append(((this.data == null)?"<null>":this.data));
sb.append(',');
if (sb.charAt((sb.length()- 1)) == ',') {
sb.setCharAt((sb.length()- 1), ']');
} else {
sb.append(']');
}
return sb.toString();
}

}