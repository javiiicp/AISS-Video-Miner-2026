package aiss.peertube_miner.model.external;

import javax.annotation.processing.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"id",
"publishedAt",
"truncatedDescription",
"name",
"account"
})
@Generated("jsonschema2pojo")
public class DataVideo {

@JsonProperty("id")
private Integer id;
@JsonProperty("publishedAt")
private String publishedAt;
@JsonProperty("truncatedDescription")
private String truncatedDescription;
@JsonProperty("name")
private String name;
@JsonProperty("account")
private ApiAccount account;

@JsonProperty("id")
public Integer getId() {
return id;
}

@JsonProperty("id")
public void setId(Integer id) {
this.id = id;
}

@JsonProperty("publishedAt")
public String getPublishedAt() {
return publishedAt;
}

@JsonProperty("publishedAt")
public void setPublishedAt(String publishedAt) {
this.publishedAt = publishedAt;
}

@JsonProperty("truncatedDescription")
public String getTruncatedDescription() {
return truncatedDescription;
}

@JsonProperty("truncatedDescription")
public void setTruncatedDescription(String truncatedDescription) {
this.truncatedDescription = truncatedDescription;
}

@JsonProperty("name")
public String getName() {
return name;
}

@JsonProperty("name")
public void setName(String name) {
this.name = name;
}

@JsonProperty("account")
public ApiAccount getAccount() {
return account;
}

@JsonProperty("account")
public void setAccount(ApiAccount account) {
this.account = account;
}

@Override
public String toString() {
StringBuilder sb = new StringBuilder();
sb.append(DataVideo.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
sb.append("id");
sb.append('=');
sb.append(((this.id == null)?"<null>":this.id));
sb.append(',');
sb.append("publishedAt");
sb.append('=');
sb.append(((this.publishedAt == null)?"<null>":this.publishedAt));
sb.append(',');
sb.append("truncatedDescription");
sb.append('=');
sb.append(((this.truncatedDescription == null)?"<null>":this.truncatedDescription));
sb.append(',');
sb.append("name");
sb.append('=');
sb.append(((this.name == null)?"<null>":this.name));
sb.append(',');
sb.append("account");
sb.append('=');
sb.append(((this.account == null)?"<null>":this.account));
sb.append(',');
if (sb.charAt((sb.length()- 1)) == ',') {
sb.setCharAt((sb.length()- 1), ']');
} else {
sb.append(']');
}
return sb.toString();
}

}