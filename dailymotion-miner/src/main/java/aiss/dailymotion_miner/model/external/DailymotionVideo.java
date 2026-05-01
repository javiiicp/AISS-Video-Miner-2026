
package aiss.dailymotion_miner.model.external   ;

import java.util.List;

import javax.annotation.processing.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"id",
"title",
"description",
"created_time",
"tags",
"owner"
})
@Generated("jsonschema2pojo")
public class DailymotionVideo {

@JsonProperty("id")
private String id;
@JsonProperty("title")
private String title;
@JsonProperty("description")
private String description;
@JsonProperty("created_time")
private Integer createdTime;
@JsonProperty("tags")
private List<Object> tags;
@JsonProperty("owner")
private String owner;
@JsonProperty("subtitle")
private String subtitle;


@JsonProperty("id")
public String getId() {
return id;
}

@JsonProperty("id")
public void setId(String id) {
this.id = id;
}

@JsonProperty("title")
public String getTitle() {
return title;
}

@JsonProperty("title")
public void setTitle(String title) {
this.title = title;
}

@JsonProperty("description")
public String getDescription() {
return description;
}

@JsonProperty("description")
public void setDescription(String description) {
this.description = description;
}

@JsonProperty("created_time")
public Integer getCreatedTime() {
return createdTime;
}

@JsonProperty("created_time")
public void setCreatedTime(Integer createdTime) {
this.createdTime = createdTime;
}

@JsonProperty("tags")
public List<Object> getTags() {
return tags;
}

@JsonProperty("tags")
public void setTags(List<Object> tags) {
this.tags = tags;
}

@JsonProperty("owner")
public String getOwner() {
return owner;
}

@JsonProperty("owner")
public void setOwner(String owner) {
this.owner = owner;
}
/*/
@JsonProperty("subtitle")
public String getSubtitle() {
return subtitle;
}

@JsonProperty("subtitle")
public void setSubtitle(String subtitle) {
this.subtitle = subtitle;
}*/ 
//Creo que esto ya no es necesario, pero aun nolo borro por si acaso

@JsonProperty("subtitles")
private List<Object> subtitles;

@JsonProperty("subtitles")
public List<Object> getSubtitles() {
    return subtitles;
}

@JsonProperty("subtitles")
public void setSubtitles(List<Object> subtitles) {
    this.subtitles = subtitles;
}

@Override
public String toString() {
StringBuilder sb = new StringBuilder();
sb.append(DailymotionVideo.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
sb.append("id");
sb.append('=');
sb.append(((this.id == null)?"<null>":this.id));
sb.append(',');
sb.append("title");
sb.append('=');
sb.append(((this.title == null)?"<null>":this.title));
sb.append(',');
sb.append("description");
sb.append('=');
sb.append(((this.description == null)?"<null>":this.description));
sb.append(',');
sb.append("createdTime");
sb.append('=');
sb.append(((this.createdTime == null)?"<null>":this.createdTime));
sb.append(',');
sb.append("tags");
sb.append('=');
sb.append(((this.tags == null)?"<null>":this.tags));
sb.append(',');
sb.append("owner");
sb.append('=');
sb.append(((this.owner == null)?"<null>":this.owner));
sb.append(',');
sb.append("subtitle");
sb.append('=');
sb.append(((this.subtitle == null)?"<null>":this.subtitle));
sb.append(',');
if (sb.charAt((sb.length()- 1)) == ',') {
sb.setCharAt((sb.length()- 1), ']');
} else {
sb.append(']');
}
return sb.toString();
}

}