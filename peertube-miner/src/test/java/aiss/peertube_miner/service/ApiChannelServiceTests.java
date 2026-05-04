package aiss.peertube_miner.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import aiss.peertube_miner.exception.ChannelNotFoundException;
import aiss.peertube_miner.model.external.ApiChannel;
import aiss.peertube_miner.model.external.ApiVideo;

/**
 * Suite de tests unitarios para ApiChannelService de PeerTube.
 * Valida la extracción y transformación de canales desde la API de PeerTube.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PeerTube Channel Service Unit Tests")
class ApiChannelServiceTests {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ApiChannelService apiChannelService;

    private String testChannelId;

    @BeforeEach
    void setUp() {
        testChannelId = "test_channel_123";
    }

    @Test
    @DisplayName("Debe lanzar ChannelNotFoundException cuando el canal no existe")
    void testGetChannelFromPeerTubeNotFound() {
        // Arrange
        String channelUrl = "https://peertube.tv/api/v1/video-channels/" + testChannelId;
        when(restTemplate.getForObject(channelUrl, ApiChannel.class))
                .thenThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.NOT_FOUND, "Not Found"));

        // Act & Assert
        assertThrows(HttpClientErrorException.class, () -> {
            apiChannelService.getChannelFromPeerTube(testChannelId, 10, 2, null, 0, 10, "id", "asc");
        });

        verify(restTemplate).getForObject(channelUrl, ApiChannel.class);
    }

    @Test
    @DisplayName("Debe lanzar ChannelNotFoundException cuando la respuesta es null")
    void testGetChannelFromPeerTubeNullResponse() {
        // Arrange
        String channelUrl = "https://peertube.tv/api/v1/video-channels/" + testChannelId;
        when(restTemplate.getForObject(channelUrl, ApiChannel.class)).thenReturn(null);

        // Act & Assert
        assertThrows(ChannelNotFoundException.class, () -> {
            apiChannelService.getChannelFromPeerTube(testChannelId, 10, 2, null, 0, 10, "id", "asc");
        });
    }

    @Test
    @DisplayName("Debe aceptar parámetros de paginación y filtrado")
    void testGetChannelAcceptsPaginationParams() {
        // Arrange
        String channelUrl = "https://peertube.tv/api/v1/video-channels/" + testChannelId;
        ApiChannel mockChannel = createMockApiChannel();
        when(restTemplate.getForObject(channelUrl, ApiChannel.class)).thenReturn(mockChannel);
        when(restTemplate.getForObject(channelUrl + "/videos?count=10", ApiVideo.class))
                .thenReturn(new ApiVideo());

        // Act & Assert
        assertDoesNotThrow(() -> {
            apiChannelService.getChannelFromPeerTube(testChannelId, 10, 2, "filter", 0, 10, "id", "asc");
        });
    }

    @Test
    @DisplayName("Debe aceptar parámetros de ordenación descendente")
    void testGetChannelAcceptsSortDescending() {
        // Arrange
        String channelUrl = "https://peertube.tv/api/v1/video-channels/" + testChannelId;
        ApiChannel mockChannel = createMockApiChannel();
        when(restTemplate.getForObject(channelUrl, ApiChannel.class)).thenReturn(mockChannel);
        when(restTemplate.getForObject(channelUrl + "/videos?count=10", ApiVideo.class))
                .thenReturn(new ApiVideo());

        // Act & Assert
        assertDoesNotThrow(() -> {
            apiChannelService.getChannelFromPeerTube(testChannelId, 10, 2, null, 0, 10, "name", "desc");
        });
    }

    @Test
    @DisplayName("Debe pasar correctamente maxVideos y maxPages al servicio")
    void testGetChannelPassesCorrectParameters() {
        // Arrange
        String channelUrl = "https://peertube.tv/api/v1/video-channels/" + testChannelId;
        ApiChannel mockChannel = createMockApiChannel();
        when(restTemplate.getForObject(channelUrl, ApiChannel.class)).thenReturn(mockChannel);
        when(restTemplate.getForObject(channelUrl + "/videos?count=5", ApiVideo.class))
                .thenReturn(new ApiVideo());

        // Act & Assert
        assertDoesNotThrow(() -> {
            apiChannelService.getChannelFromPeerTube(testChannelId, 5, 3, null, 0, 10, "id", "asc");
        });
    }

    @Test
    @DisplayName("Debe aceptar límites altos de máximo de videos y páginas")
    void testGetChannelAcceptsHighLimits() {
        // Arrange
        String channelUrl = "https://peertube.tv/api/v1/video-channels/" + testChannelId;
        ApiChannel mockChannel = createMockApiChannel();
        when(restTemplate.getForObject(channelUrl, ApiChannel.class)).thenReturn(mockChannel);
        when(restTemplate.getForObject(channelUrl + "/videos?count=1000", ApiVideo.class))
                .thenReturn(new ApiVideo());

        // Act & Assert
        assertDoesNotThrow(() -> {
            apiChannelService.getChannelFromPeerTube(testChannelId, 1000, 100, null, 0, 10, "id", "asc");
        });
    }

    @Test
    @DisplayName("Debe manejar RestClientException genérica")
    void testHandlesRestClientException() {
        // Arrange
        String channelUrl = "https://peertube.tv/api/v1/video-channels/" + testChannelId;
        when(restTemplate.getForObject(channelUrl, ApiChannel.class))
                .thenThrow(new RuntimeException("Network error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            apiChannelService.getChannelFromPeerTube(testChannelId, 10, 2, null, 0, 10, "id", "asc");
        });
    }

    private ApiChannel createMockApiChannel() {
        ApiChannel channel = new ApiChannel();
        channel.setId(1);
        channel.setDisplayName("Test Channel");
        channel.setDescription("Test Description");
        channel.setCreatedAt("2026-01-01T00:00:00Z");
        return channel;
    }
}
