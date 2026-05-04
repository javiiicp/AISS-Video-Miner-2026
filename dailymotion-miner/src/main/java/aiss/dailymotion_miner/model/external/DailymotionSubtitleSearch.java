package aiss.dailymotion_miner.model.external;

import java.util.List;

import javax.annotation.processing.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"subtitles"
})
@Generated("jsonschema2pojo")
public class DailymotionSubtitleSearch {

    @JsonProperty("subtitles")
    private List<DailymotionSubtitle> subtitles;

    public DailymotionSubtitleSearch() {}

    @JsonProperty("subtitles")
    public List<DailymotionSubtitle> getSubtitles() {
        return subtitles;
    }

    @JsonProperty("subtitles")
    public void setSubtitles(List<DailymotionSubtitle> subtitles) {
        this.subtitles = subtitles;
    }

    @Override
    public String toString() {
        return "DailymotionSubtitleSearch{subtitles=" + subtitles + '}';
    }
}