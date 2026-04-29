package aiss.dailymotion_miner.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DailymotionVideo {
    @JsonProperty("id")
    private String id;
    @JsonProperty("title") 
    private String title;
    @JsonProperty("created_time")
    private Long createdTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Long getCreatedTime() { return createdTime; }
    public void setCreatedTime(Long createdTime) { this.createdTime = createdTime; }
}