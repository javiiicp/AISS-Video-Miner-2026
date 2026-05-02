package aiss.dailymotion_miner.model.external;

import java.util.List;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"tags",
"created_time"
})
@Generated("jsonschema2pojo")
public class DailymotionComment {

@JsonProperty("tags")
private List<String> tags;
@JsonProperty("created_time")
private Integer createdTime;

@JsonProperty("tags")
public List<String> getTags() {
return tags;
}

@JsonProperty("tags")
public void setTags(List<String> tags) {
this.tags = tags;
}

@JsonProperty("created_time")
public Integer getCreatedTime() {
return createdTime;
}

@JsonProperty("created_time")
public void setCreatedTime(Integer createdTime) {
this.createdTime = createdTime;
}

@Override
public String toString() {
StringBuilder sb = new StringBuilder();
sb.append(DailymotionComment.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
sb.append("tags");
sb.append('=');
sb.append(((this.tags == null)?"<null>":this.tags));
sb.append(',');
sb.append("createdTime");
sb.append('=');
sb.append(((this.createdTime == null)?"<null>":this.createdTime));
sb.append(',');
if (sb.charAt((sb.length()- 1)) == ',') {
sb.setCharAt((sb.length()- 1), ']');
} else {
sb.append(']');
}
return sb.toString();
}

}