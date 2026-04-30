
package aiss.dailymotion_miner.model.external;

import javax.annotation.processing.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"id",
"language",
"link"
})
@Generated("jsonschema2pojo")
public class DailymotionSubtitle {

@JsonProperty("id")
private String id;
@JsonProperty("language")
private String language;
@JsonProperty("link")
private String link;

@JsonProperty("id")
public String getId() {
return id;
}

@JsonProperty("id")
public void setId(String id) {
this.id = id;
}

@JsonProperty("language")
public String getLanguage() {
return language;
}

@JsonProperty("language")
public void setLanguage(String language) {
this.language = language;
}

@JsonProperty("link")
public String getLink() {
return link;
}

@JsonProperty("link")
public void setLink(String link) {
this.link = link;
}

@Override
public String toString() {
StringBuilder sb = new StringBuilder();
sb.append(DailymotionSubtitle.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
sb.append("id");
sb.append('=');
sb.append(((this.id == null)?"<null>":this.id));
sb.append(',');
sb.append("language");
sb.append('=');
sb.append(((this.language == null)?"<null>":this.language));
sb.append(',');
sb.append("link");
sb.append('=');
sb.append(((this.link == null)?"<null>":this.link));
sb.append(',');
if (sb.charAt((sb.length()- 1)) == ',') {
sb.setCharAt((sb.length()- 1), ']');
} else {
sb.append(']');
}
return sb.toString();
}

}