package aiss.peertube_miner.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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

    @Autowired
    private ApiCommentService commentService;

    @Autowired
    private ApiVideoUserService videoUserService;

    @Autowired
    private ApiCaptionService captionService;

    /**
     * Obtiene un canal de PeerTube por su handle y procesa sus vídeos con filtros y paginación.
     * @param handle El nombre corto del canal (slug).
     */
    public Channel getChannelFromPeerTube(String handle, int maxVideos, int maxComments, String name, int page, int size,
            String sortBy, String sortDir) {

        // 1. Identificación por nombre (handle) según documentación real de PeerTube
        String urlCanal = "https://peertube.tv/api/v1/video-channels/" + handle;
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

        // Transformamos metadatos básicos del canal
        Channel videominerChannel = PeertubeMapper.toChannel(resCanal);

        // 2. Extracción de vídeos (usando el handle para la búsqueda)
        // Obtenemos los vídeos brutos delegando en el servicio de usuario/vídeos
        List<Video> listaVideosBruta = videoUserService.getVideoUser(handle, maxVideos);

        // 3. Procesamiento profundo de cada vídeo (comentarios y subtítulos)
        for (Video video : listaVideosBruta) {
            video.setComments(commentService.getComments(video.getId(), maxComments));
            video.setCaptions(captionService.getCaption(video.getId()));
        }

        // 4. Aplicación de Paginación, Filtrado y Ordenación en memoria
        // Como pedimos un solo canal, estos parámetros operan sobre su lista de vídeos
        List<Video> videosProcesados = applyVideoFilters(listaVideosBruta, name, page, size, sortBy, sortDir);
        
        videominerChannel.setVideos(videosProcesados);
        return videominerChannel;
    }

    /**
     * Lógica de filtrado y paginación sobre la colección de vídeos del canal.
     */
    private List<Video> applyVideoFilters(List<Video> videos, String name, int page, int size, String sortBy, String sortDir) {
        List<Video> filtered = new ArrayList<>(videos);

        // Filtrado por nombre (búsqueda parcial)
        if (name != null && !name.isBlank()) {
            String nameFilter = name.toLowerCase(Locale.ROOT);
            filtered.removeIf(v -> v.getName() == null || !v.getName().toLowerCase(Locale.ROOT).contains(nameFilter));
        }

        // Configuración del comparador para ordenación
        Comparator<Video> comparator;
        if ("name".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(v -> safeString(v.getName()), String.CASE_INSENSITIVE_ORDER);
        } else if ("releaseTime".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(v -> safeString(v.getReleaseTime()));
        } else {
            comparator = Comparator.comparing(v -> safeString(v.getId()));
        }

        // Dirección del orden
        if ("desc".equalsIgnoreCase(sortDir)) {
            filtered.sort(comparator.reversed());
        } else {
            filtered.sort(comparator);
        }

        // Paginación manual
        int fromIndex = Math.min(page * size, filtered.size());
        int toIndex = Math.min(fromIndex + size, filtered.size());
        
        return new ArrayList<>(filtered.subList(fromIndex, toIndex));
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}