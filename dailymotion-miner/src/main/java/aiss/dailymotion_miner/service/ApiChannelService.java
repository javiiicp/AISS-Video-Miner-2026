package aiss.dailymotion_miner.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import aiss.dailymotion_miner.exception.ChannelNotFoundException;
import aiss.dailymotion_miner.mapper.DailymotionMapper;
import aiss.dailymotion_miner.model.Channel;
import aiss.dailymotion_miner.model.Video;
import aiss.dailymotion_miner.model.external.DailymotionPlaylist;
import aiss.dailymotion_miner.model.external.DailymotionVideo;
import aiss.dailymotion_miner.model.external.DailymotionVideoSearch;

@Service
public class ApiChannelService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ApiVideoUserService userService;
    @Autowired
    private ApiCommentService commentService;
    @Autowired
    private ApiSubtitleService subtitleService;

    /**
     * Extrae, transforma y filtra un canal desde Dailymotion.
     */
    public Channel getChannelFromDailymotion(String playlistId, int maxVideos, int maxComments, 
                                             String name, int page, int size, String sortBy, String sortDir) {
        
        // 1. Obtener metadatos de la Playlist (Canal)
        String urlPlaylist = "https://api.dailymotion.com/playlist/" + playlistId + "?fields=id,name,description,created_time";
        DailymotionPlaylist extPlaylist;
        try {
            extPlaylist = restTemplate.getForObject(urlPlaylist, DailymotionPlaylist.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ChannelNotFoundException();
        }

        if (extPlaylist == null) throw new ChannelNotFoundException();
        Channel channel = DailymotionMapper.toChannel(extPlaylist);

        // 2. Extraer lista de vídeos (limitada por maxVideos)
        String urlVideos = "https://api.dailymotion.com/playlist/" + playlistId + "/videos?limit=" + maxVideos + "&fields=id,title,description,created_time,owner,tags";
        DailymotionVideoSearch videoSearch = restTemplate.getForObject(urlVideos, DailymotionVideoSearch.class);

        List<Video> listaVideosLimpia = new ArrayList<>();
        if (videoSearch != null && videoSearch.getList() != null) {
            for (DailymotionVideo extVideo : videoSearch.getList()) {
                Video video = DailymotionMapper.toVideo(extVideo);
                
                // Enriquecimiento relacional usando servicios especializados
                video.setAuthor(userService.getAuthor(extVideo.getOwner()));
                video.setComments(commentService.getComments(extVideo.getTags(), video.getId()));
                video.setCaptions(subtitleService.getSubtitles(video.getId()));

                listaVideosLimpia.add(video);
            }
        }

        // 3. Aplicar lógica de Filtrado, Ordenación y Paginación solicitada por el Controller
        List<Video> videosProcesados = applyVideoFilters(listaVideosLimpia, name, page, size, sortBy, sortDir);
        channel.setVideos(videosProcesados);

        return channel;
    }

    /**
     * Realiza el filtrado, ordenación y paginación en memoria para cumplir con los requisitos de la API.
     */
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

        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }
        filtered.sort(comparator);

        // Paginación manual segura (evita IndexOutOfBoundsException)
        int fromIndex = Math.min(page * size, filtered.size());
        int toIndex = Math.min(fromIndex + size, filtered.size());
        
        return new ArrayList<>(filtered.subList(fromIndex, toIndex));
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}