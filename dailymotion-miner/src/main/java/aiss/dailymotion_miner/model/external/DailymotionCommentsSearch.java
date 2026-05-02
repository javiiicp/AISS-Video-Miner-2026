package aiss.dailymotion_miner.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DailymotionCommentsSearch {

    @JsonProperty("tags")
    private List<String> tags; // Aquí caerán las etiquetas del vídeo

    @JsonProperty("created_time")
    private String createdTime;
}
