package aiss.dailymotion_miner.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import aiss.dailymotion_miner.exception.ChannelNotFoundException;
import aiss.dailymotion_miner.model.Channel;
import aiss.dailymotion_miner.model.external.DailymotionPlaylist;
import aiss.dailymotion_miner.model.external.DailymotionVideoSearch;

@ExtendWith(MockitoExtension.class)
@DisplayName("Dailymotion Channel Service Unit Tests")
class ApiChannelServiceTests {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ApiChannelService apiChannelService;

    private String playlistId;

    @BeforeEach
    void setUp() {
        playlistId = "test_playlist_123";
    }

    @Test
    @DisplayName("Debe extraer playlist y retornar Channel con videos consolidados")
    void testGetChannelFromDailymotionSuccess() {
        // Arrange
        String urlPlaylist = "https://api.dailymotion.com/playlist/" + playlistId;
        DailymotionPlaylist playlist = new DailymotionPlaylist();
        playlist.setId("pl_123");
        playlist.setName("Test Playlist");
        playlist.setDescription("Test Description");
        playlist.setCreatedTime(1710000000L);

        when(restTemplate.getForObject(urlPlaylist, DailymotionPlaylist.class)).thenReturn(playlist);
        
        String urlVideos = "https://api.dailymotion.com/playlist/" + playlistId + "/videos?limit=10";
        DailymotionVideoSearch videos = new DailymotionVideoSearch();
        when(restTemplate.getForObject(urlVideos, DailymotionVideoSearch.class)).thenReturn(videos);

        // Act
        Channel result = apiChannelService.getChannelFromDailymotion(playlistId, 10, 2);

        // Assert
        assertNotNull(result);
        assertEquals("pl_123", result.getId());
        assertEquals("Test Playlist", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertNotNull(result.getVideos());
        
        verify(restTemplate).getForObject(urlPlaylist, DailymotionPlaylist.class);
        verify(restTemplate).getForObject(urlVideos, DailymotionVideoSearch.class);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la playlist no existe")
    void testGetChannelFromDailymotionNotFound() {
        // Arrange
        String urlPlaylist = "https://api.dailymotion.com/playlist/" + playlistId;
        when(restTemplate.getForObject(urlPlaylist, DailymotionPlaylist.class))
            .thenThrow(HttpClientErrorException.NotFound.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                null,
                null,
                null));

        // Act & Assert
        ChannelNotFoundException exception = assertThrows(ChannelNotFoundException.class,
            () -> apiChannelService.getChannelFromDailymotion(playlistId, 10, 2));

        assertEquals("El canal no existe en Dailymotion", exception.getMessage());
        verify(restTemplate).getForObject(urlPlaylist, DailymotionPlaylist.class);
    }

    @Test
    @DisplayName("Debe retornar null cuando la respuesta de playlist es null")
    void testGetChannelFromDailymotionNullResponse() {
        // Arrange
        String urlPlaylist = "https://api.dailymotion.com/playlist/" + playlistId;
        when(restTemplate.getForObject(urlPlaylist, DailymotionPlaylist.class)).thenReturn(null);

        // Act
        Channel result = apiChannelService.getChannelFromDailymotion(playlistId, 10, 2);

        // Assert
        assertNull(result);
        verify(restTemplate).getForObject(urlPlaylist, DailymotionPlaylist.class);
    }

    @Test
    @DisplayName("Debe aceptar diferentes parámetros de maxVideos")
    void testGetChannelFromDailymotionAcceptsMaxVideos() {
        // Arrange
        String urlPlaylist = "https://api.dailymotion.com/playlist/" + playlistId;
        DailymotionPlaylist playlist = new DailymotionPlaylist();
        playlist.setId("pl_123");
        playlist.setName("Test Playlist");

        when(restTemplate.getForObject(urlPlaylist, DailymotionPlaylist.class)).thenReturn(playlist);
        
        String urlVideos = "https://api.dailymotion.com/playlist/" + playlistId + "/videos?limit=20";
        DailymotionVideoSearch videos = new DailymotionVideoSearch();
        when(restTemplate.getForObject(urlVideos, DailymotionVideoSearch.class)).thenReturn(videos);

        // Act
        Channel result = apiChannelService.getChannelFromDailymotion(playlistId, 20, 2);

        // Assert
        assertNotNull(result);
        verify(restTemplate).getForObject(urlVideos, DailymotionVideoSearch.class);
    }

    @Test
    @DisplayName("Debe construir URLs correctamente con diferentes IDs de playlist")
    void testUrlConstruction() {
        // Arrange
        String testId = "my-playlist-456";
        String urlPlaylist = "https://api.dailymotion.com/playlist/" + testId;
        DailymotionPlaylist playlist = new DailymotionPlaylist();
        playlist.setId("pl_456");
        playlist.setName("Another Playlist");

        when(restTemplate.getForObject(urlPlaylist, DailymotionPlaylist.class)).thenReturn(playlist);
        
        String urlVideos = "https://api.dailymotion.com/playlist/" + testId + "/videos?limit=10";
        DailymotionVideoSearch videos = new DailymotionVideoSearch();
        when(restTemplate.getForObject(urlVideos, DailymotionVideoSearch.class)).thenReturn(videos);

        // Act
        Channel result = apiChannelService.getChannelFromDailymotion(testId, 10, 2);

        // Assert
        assertNotNull(result);
        assertEquals("pl_456", result.getId());
        verify(restTemplate).getForObject(urlPlaylist, DailymotionPlaylist.class);
    }
}
