package aiss.peertube_miner.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import aiss.peertube_miner.exception.ChannelNotFoundException;
import aiss.peertube_miner.mapper.PeertubeMapper;
import aiss.peertube_miner.model.Channel;
import aiss.peertube_miner.model.Video;
import aiss.peertube_miner.model.external.ApiChannel;

@Service
public class ApiChannelService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApiVideoUserService videoUserService;

    @Autowired
    private ApiCommentService commentService;

    @Autowired
    private ApiCaptionService captionService;

    public Channel getChannelFromPeerTube(String channelId, int maxVideos, int maxComments, 
                                          String name, int page, int size, String sortBy, String sortDir) {

        String urlCanal = "https://peertube.tv/api/v1/video-channels/" + channelId;
        ApiChannel resCanal;
        try {
            resCanal = restTemplate.getForObject(urlCanal, ApiChannel.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ChannelNotFoundException();
        }

        if (resCanal == null) throw new ChannelNotFoundException();

        // 1. Transformar Canal base
        Channel videominerChannel = PeertubeMapper.toChannel(resCanal);

        // 2. Extraer Vídeos y Autores usando el servicio de video
        List<Video> videosExtraidos = videoUserService.getVideoUser(channelId, maxVideos);

        // 3. Añadir a cada vídeo  Comentarios y Subtítulos
        for (Video video : videosExtraidos) {
            video.setComments(commentService.getComments(video.getId(), maxComments));
            video.setCaptions(captionService.getCaption(video.getId()));
        }

        // 4. Aplicar Filtrado, Ordenación y Paginación solicitada por el Controller
        List<Video> videosProcesados = applyVideoFilters(videosExtraidos, name, page, size, sortBy, sortDir);
        videominerChannel.setVideos(videosProcesados);

        return videominerChannel;
    }

    private List<Video> applyVideoFilters(List<Video> videos, String name, int page, int size, String sortBy, String sortDir) {
        List<Video> filtered = new ArrayList<>(videos);

        // Filtrado por nombre (insensible a mayúsculas)
        if (name != null && !name.isBlank()) {
            String nameFilter = name.toLowerCase(Locale.ROOT);
            filtered.removeIf(v -> v.getName() == null || !v.getName().toLowerCase(Locale.ROOT).contains(nameFilter));
        }

        // Ordenación dinámica
        Comparator<Video> comparator = switch (sortBy.toLowerCase()) {
            case "name" -> Comparator.comparing(v -> safeString(v.getName()), String.CASE_INSENSITIVE_ORDER);
            case "releasetime" -> Comparator.comparing(v -> safeString(v.getReleaseTime()));
            default -> Comparator.comparing(v -> safeString(v.getId()));
        };

        if ("desc".equalsIgnoreCase(sortDir)) comparator = comparator.reversed();
        filtered.sort(comparator);

        // Paginación manual (subList segura)
        int fromIndex = Math.min(page * size, filtered.size());
        int toIndex = Math.min(fromIndex + size, filtered.size());
        
        return new ArrayList<>(filtered.subList(fromIndex, toIndex));
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}