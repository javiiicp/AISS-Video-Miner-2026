package aiss.peertube_miner.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import aiss.peertube_miner.exception.ChannelNotFoundException;
import aiss.peertube_miner.mapper.PeertubeMapper;
import aiss.peertube_miner.model.Caption;
import aiss.peertube_miner.model.Channel;
import aiss.peertube_miner.model.Comment;
import aiss.peertube_miner.model.Video;
import aiss.peertube_miner.model.external.ApiCaption;
import aiss.peertube_miner.model.external.ApiChannel;
import aiss.peertube_miner.model.external.ApiComment;
import aiss.peertube_miner.model.external.ApiVideo;
import aiss.peertube_miner.model.external.DataCaption;
import aiss.peertube_miner.model.external.DataComment;
import aiss.peertube_miner.model.external.DataVideo;

@Service
public class ApiChannelService {

    @Autowired
    private RestTemplate restTemplate;

    public Channel getChannelFromPeerTube(String channelId, int maxVideos, int maxComments, String name, int page, int size,
            String sortBy, String sortDir) {

        String urlCanal = "https://peertube.tv/api/v1/video-channels/" + channelId;
        ApiChannel resCanal;
        try {
            resCanal = restTemplate.getForObject(urlCanal, ApiChannel.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ChannelNotFoundException();
        } catch (RestClientException ex) {
            throw ex;
        }

        if (resCanal == null) {
            throw new ChannelNotFoundException();
        }

        Channel videominerChannel = PeertubeMapper.toChannel(resCanal);

        String urlVideos = urlCanal + "/videos?count=" + maxVideos;
        ApiVideo resVideos = restTemplate.getForObject(urlVideos, ApiVideo.class);

        List<Video> listaVideosLimpia = new ArrayList<>();
        if (resVideos != null && resVideos.getData() != null) {
            for (DataVideo ptVideo : resVideos.getData()) {
                Video video = PeertubeMapper.toVideo(ptVideo);
                if (ptVideo.getAccount() != null) {
                    video.setAuthor(PeertubeMapper.toUser(ptVideo.getAccount()));
                }

                List<Comment> comments = new ArrayList<>();
                try {
                    String urlComments = "https://peertube.tv/api/v1/videos/" + ptVideo.getId() + "/comment-threads?count=" + maxComments;
                    ApiComment resComments = restTemplate.getForObject(urlComments, ApiComment.class);
                    if (resComments != null && resComments.getData() != null) {
                        for (DataComment ptComment : resComments.getData()) {
                            comments.add(PeertubeMapper.toComment(ptComment));
                        }
                    }
                } catch (RestClientException ex) {
                    comments = new ArrayList<>();
                }
                video.setComments(comments);

                List<Caption> captions = new ArrayList<>();
                try {
                    String urlCaptions = "https://peertube.tv/api/v1/videos/" + ptVideo.getId() + "/captions";
                    ApiCaption resCaptions = restTemplate.getForObject(urlCaptions, ApiCaption.class);
                    if (resCaptions != null && resCaptions.getData() != null) {
                        for (DataCaption ptCaption : resCaptions.getData()) {
                            captions.add(PeertubeMapper.toCaption(ptCaption));
                        }
                    }
                } catch (RestClientException ex) {
                    captions = new ArrayList<>();
                }
                video.setCaptions(captions);

                listaVideosLimpia.add(video);
            }
        }

        videominerChannel.setVideos(listaVideosLimpia);
        return videominerChannel;
    }


    private String safeString(String value) {
        return value == null ? "" : value;
    }
}
