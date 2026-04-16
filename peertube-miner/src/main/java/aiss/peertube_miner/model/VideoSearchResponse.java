package aiss.peertube_miner.model;

import java.util.List;

public class VideoSearchResponse {
    private List<Video> data; // PeerTube envía los vídeos aquí
    public List<Video> getData() { return data; }
    public void setData(List<Video> data) { this.data = data; }
}