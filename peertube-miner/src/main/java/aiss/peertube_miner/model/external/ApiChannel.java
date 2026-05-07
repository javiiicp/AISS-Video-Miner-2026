package aiss.peertube_miner.model.external;

import javax.annotation.processing.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"id",
"url",
"name",
"createdAt",
"updatedAt",
"displayName",
"description"
})
@Generated("jsonschema2pojo")
public class ApiChannel {

@JsonProperty("id")
private Integer id;
@JsonProperty("url")
private String url;
@JsonProperty("name")
private String name;
@JsonProperty("createdAt")
private String createdAt;
@JsonProperty("updatedAt")
private String updatedAt;
@JsonProperty("displayName")
private String displayName;
@JsonProperty("description")
private String description;

@JsonProperty("id")
public Integer getId() {
return id;
}

@JsonProperty("id")
public void setId(Integer id) {
this.id = id;
}

@JsonProperty("url")
public String getUrl() {
return url;
}

@JsonProperty("url")
public void setUrl(String url) {
this.url = url;
}

@JsonProperty("name")
public String getName() {
return name;
}

@JsonProperty("name")
public void setName(String name) {
this.name = name;
}

@JsonProperty("createdAt")
public String getCreatedAt() {
return createdAt;
}

@JsonProperty("createdAt")
public void setCreatedAt(String createdAt) {
this.createdAt = createdAt;
}

@JsonProperty("updatedAt")
public String getUpdatedAt() {
return updatedAt;
}

@JsonProperty("updatedAt")
public void setUpdatedAt(String updatedAt) {
this.updatedAt = updatedAt;
}

@JsonProperty("displayName")
public String getDisplayName() {
return displayName;
}

@JsonProperty("displayName")
public void setDisplayName(String displayName) {
this.displayName = displayName;
}

@JsonProperty("description")
public String getDescription() {
return description;
}

@JsonProperty("description")
public void setDescription(String description) {
this.description = description;
}

@Override
public String toString() {
StringBuilder sb = new StringBuilder();
sb.append(ApiChannel.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
sb.append("id");
sb.append('=');
sb.append(((this.id == null)?"<null>":this.id));
sb.append(',');
sb.append("url");
sb.append('=');
sb.append(((this.url == null)?"<null>":this.url));
sb.append(',');
sb.append("name");
sb.append('=');
sb.append(((this.name == null)?"<null>":this.name));
sb.append(',');
sb.append("createdAt");
sb.append('=');
sb.append(((this.createdAt == null)?"<null>":this.createdAt));
sb.append(',');
sb.append("updatedAt");
sb.append('=');
sb.append(((this.updatedAt == null)?"<null>":this.updatedAt));
sb.append(',');
sb.append("displayName");
sb.append('=');
sb.append(((this.displayName == null)?"<null>":this.displayName));
sb.append(',');
sb.append("description");
sb.append('=');
sb.append(((this.description == null)?"<null>":this.description));
sb.append(',');
if (sb.charAt((sb.length()- 1)) == ',') {
sb.setCharAt((sb.length()- 1), ']');
} else {
sb.append(']');
}
return sb.toString();
}

}