package aiss.peertube_miner.service;

<<<<<<< HEAD
=======
import java.util.List;
import java.util.Locale;

>>>>>>> eb91d1e (5. Alinear PeerTubeMiner al PDF y añadir OAS y paginacion)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestClientException;

import aiss.peertube_miner.mapper.PeertubeMapper;
import aiss.peertube_miner.model.Channel;
import aiss.peertube_miner.model.external.ApiChannel;

import aiss.peertube_miner.model.Caption;
import aiss.peertube_miner.model.Comment;
import aiss.peertube_miner.model.Video;
@Service
import aiss.peertube_miner.model.external.ApiCaption;
import aiss.peertube_miner.model.external.ApiComment;
import aiss.peertube_miner.model.external.ApiVideo;
import aiss.peertube_miner.model.external.DataCaption;
import aiss.peertube_miner.model.external.DataComment;
import aiss.peertube_miner.model.external.DataVideo;
public class ApiChannelService {

    @Autowired
    RestTemplate restTemplate;

    private RestTemplate restTemplate;

    public Channel getChannelFromPeerTube(String channelId, int maxVideos, int maxComments, String name, int page, int size,
            String sortBy, String sortDir) {
        ApiChannel resCanal;
        try {
            resCanal = restTemplate.getForObject(urlCanal, ApiChannel.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Canal no encontrado en PeerTube", ex);
        }

        } catch (RestClientException ex) {
            throw ex;
        if (resCanal == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Canal no encontrado en PeerTube");
        }
        
        return PeertubeMapper.toChannel(resCanal);

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

        videominerChannel.setVideos(applyVideoFilters(listaVideosLimpia, name, page, size, sortBy, sortDir));
        return videominerChannel;
    }

    private List<Video> applyVideoFilters(List<Video> videos, String name, int page, int size, String sortBy, String sortDir) {
        List<Video> filtered = new ArrayList<>(videos);

        if (name != null && !name.isBlank()) {
            String nameFilter = name.toLowerCase(Locale.ROOT);
            filtered.removeIf(video -> video.getName() == null || !video.getName().toLowerCase(Locale.ROOT).contains(nameFilter));
        }

        Comparator<Video> comparator;
        if ("name".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(video -> safeString(video.getName()), String.CASE_INSENSITIVE_ORDER);
        } else if ("releaseTime".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(video -> safeString(video.getReleaseTime()));
        } else {
            comparator = Comparator.comparing(video -> safeString(video.getId()));
        }

        filtered.sort(comparator);
        if ("desc".equalsIgnoreCase(sortDir)) {
            filtered = new ArrayList<>(filtered.reversed());
        }

        int fromIndex = Math.min(page * size, filtered.size());
        int toIndex = Math.min(fromIndex + size, filtered.size());
        return new ArrayList<>(filtered.subList(fromIndex, toIndex));
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}

        if (name != null && !name.isBlank()) {
            String nameFilter = name.toLowerCase(Locale.ROOT);
            filtered.removeIf(v -> v.getName() == null || !v.getName().toLowerCase(Locale.ROOT).contains(nameFilter));
        }

        Comparator<Video> comparator;
        if ("name".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(v -> safeString(v.getName()), String.CASE_INSENSITIVE_ORDER);
        } else if ("releaseTime".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(v -> safeString(v.getReleaseTime()));
        } else {
            comparator = Comparator.comparing(v -> safeString(v.getId()));
        }

        filtered.sort(comparator);
        if ("desc".equalsIgnoreCase(sortDir)) {
            filtered.sort(comparator.reversed());
        }

        int fromIndex = Math.min(page * size, filtered.size());
        int toIndex = Math.min(fromIndex + size, filtered.size());
        return new ArrayList<>(filtered.subList(fromIndex, toIndex));
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}
>>>>>>> eb91d1e (5. Alinear PeerTubeMiner al PDF y añadir OAS y paginacion)
