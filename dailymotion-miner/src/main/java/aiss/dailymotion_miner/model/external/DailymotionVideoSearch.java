package aiss.dailymotion_miner.model.external;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DailymotionVideoSearch {

    @JsonProperty("list") 
    private List<DailymotionVideo> list;

    public List<DailymotionVideo> getList() { return list; }
    public void setList(List<DailymotionVideo> list) { this.list = list; }
}