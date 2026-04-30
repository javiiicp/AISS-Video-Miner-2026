
package aiss.dailymotion_miner.model.external;

import javax.annotation.processing.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"id",
"username",
"url",
"avatar_120_url"
})
@Generated("jsonschema2pojo")
public class DailymotionUser {

@JsonProperty("id")
private String id;
@JsonProperty("username")
private String username;
@JsonProperty("url")
private String url;
@JsonProperty("avatar_120_url")
private String avatar120Url;

@JsonProperty("id")
public String getId() {
return id;
}

@JsonProperty("id")
public void setId(String id) {
this.id = id;
}

@JsonProperty("username")
public String getUsername() {
return username;
}

@JsonProperty("username")
public void setUsername(String username) {
this.username = username;
}

@JsonProperty("url")
public String getUrl() {
return url;
}

@JsonProperty("url")
public void setUrl(String url) {
this.url = url;
}

@JsonProperty("avatar_120_url")
public String getAvatar120Url() {
return avatar120Url;
}

@JsonProperty("avatar_120_url")
public void setAvatar120Url(String avatar120Url) {
this.avatar120Url = avatar120Url;
}

@Override
public String toString() {
StringBuilder sb = new StringBuilder();
sb.append(DailymotionUser.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
sb.append("id");
sb.append('=');
sb.append(((this.id == null)?"<null>":this.id));
sb.append(',');
sb.append("username");
sb.append('=');
sb.append(((this.username == null)?"<null>":this.username));
sb.append(',');
sb.append("url");
sb.append('=');
sb.append(((this.url == null)?"<null>":this.url));
sb.append(',');
sb.append("avatar120Url");
sb.append('=');
sb.append(((this.avatar120Url == null)?"<null>":this.avatar120Url));
sb.append(',');
if (sb.charAt((sb.length()- 1)) == ',') {
sb.setCharAt((sb.length()- 1), ']');
} else {
sb.append(']');
}
return sb.toString();
}

}