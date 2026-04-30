package aiss.dailymotion_miner.model.external;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DailymotionSubtitleSearch {

    @JsonProperty("subtitles") 
    private List<DailymotionSubtitle> subtitles;

    public List<DailymotionSubtitle> getSubtitles() { return subtitles; }
    public void setSubtitles(List<DailymotionSubtitle> subtitles) { this.subtitles = subtitles; }
}