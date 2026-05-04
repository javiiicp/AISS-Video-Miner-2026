package aiss.peertube_miner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import aiss.peertube_miner.exception.ChannelNotFoundException;
import aiss.peertube_miner.model.Channel;
import aiss.peertube_miner.service.ApiChannelService;
import aiss.peertube_miner.service.VideoMinerService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

/**
 * Suite de tests de integración para PeerTube Miner API.
 * Prueba los endpoints GET/POST del controlador de canales.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("PeerTube Miner API Integration Tests")
class PeertubeMinerApiTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiChannelService channelService;

    @MockBean
    private VideoMinerService videoMinerService;

    @MockBean
    private RestTemplate restTemplate;

    private Channel testChannel;

    @BeforeEach
    void setUp() {
        // Crear un canal de prueba
        testChannel = new Channel();
        testChannel.setId("channel_123");
        testChannel.setName("Test Channel");
        testChannel.setDescription("Test Description");
        testChannel.setCreatedTime("2026-01-01T00:00:00Z");
        testChannel.setVideos(new ArrayList<>());
    }

    // ============ Tests del Endpoint GET ============

    @Test
    @DisplayName("Debe retornar 200 al obtener un canal válido")
    void testGetChannelWithValidId() throws Exception {
        // Arrange
        when(channelService.getChannelFromPeerTube(
                eq("channel_123"),
                anyInt(), anyInt(), isNull(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(testChannel);

        // Act & Assert
        mockMvc.perform(get("/api/channels/channel_123")
                .param("maxVideos", "10")
                .param("maxComments", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("channel_123"))
                .andExpect(jsonPath("$.name").value("Test Channel"))
                .andExpect(jsonPath("$.description").value("Test Description"));

        verify(channelService).getChannelFromPeerTube(
                eq("channel_123"),
                eq(10), eq(2), isNull(), eq(0), eq(10), eq("id"), eq("asc"));
    }

    @Test
    @DisplayName("Debe retornar 404 cuando el canal no existe")
    void testGetChannelNotFound() throws Exception {
        // Arrange
        when(channelService.getChannelFromPeerTube(
                eq("missing_channel"),
                anyInt(), anyInt(), isNull(), anyInt(), anyInt(), anyString(), anyString()))
                .thenThrow(new ChannelNotFoundException());

        // Act & Assert
        mockMvc.perform(get("/api/channels/missing_channel")
                .param("maxVideos", "10")
                .param("maxComments", "2"))
                .andExpect(status().isNotFound());

        verify(channelService).getChannelFromPeerTube(
                eq("missing_channel"),
                anyInt(), anyInt(), isNull(), anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe respetar parámetros de paginación en GET")
    void testGetChannelWithPagination() throws Exception {
        // Arrange
        when(channelService.getChannelFromPeerTube(
                eq("channel_123"),
                eq(5), eq(3), isNull(), eq(1), eq(5), eq("id"), eq("desc")))
                .thenReturn(testChannel);

        // Act & Assert
        mockMvc.perform(get("/api/channels/channel_123")
                .param("maxVideos", "5")
                .param("maxComments", "3")
                .param("page", "1")
                .param("size", "5")
                .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("channel_123"));

        verify(channelService).getChannelFromPeerTube(
                eq("channel_123"),
                eq(5), eq(3), isNull(), eq(1), eq(5), eq("id"), eq("desc"));
    }

    @Test
    @DisplayName("Debe aplicar filtro por nombre en GET")
    void testGetChannelWithNameFilter() throws Exception {
        // Arrange
        when(channelService.getChannelFromPeerTube(
                eq("channel_123"),
                eq(10), eq(2), eq("test"), eq(0), eq(10), eq("id"), eq("asc")))
                .thenReturn(testChannel);

        // Act & Assert
        mockMvc.perform(get("/api/channels/channel_123")
                .param("maxVideos", "10")
                .param("maxComments", "2")
                .param("name", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("channel_123"));

        verify(channelService).getChannelFromPeerTube(
                eq("channel_123"),
                eq(10), eq(2), eq("test"), eq(0), eq(10), eq("id"), eq("asc"));
    }

    @Test
    @DisplayName("Debe usar valores por defecto cuando no se especifican parámetros")
    void testGetChannelWithDefaultParameters() throws Exception {
        // Arrange
        when(channelService.getChannelFromPeerTube(
                eq("channel_123"),
                eq(10), eq(2), isNull(), eq(0), eq(10), eq("id"), eq("asc")))
                .thenReturn(testChannel);

        // Act & Assert
        mockMvc.perform(get("/api/channels/channel_123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("channel_123"));

        verify(channelService).getChannelFromPeerTube(
                eq("channel_123"),
                eq(10), eq(2), isNull(), eq(0), eq(10), eq("id"), eq("asc"));
    }

    // ============ Tests del Endpoint POST ============

    @Test
    @DisplayName("Debe retornar 201 al guardar un canal exitosamente")
    void testPostChannelSuccessfully() throws Exception {
        // Arrange
        when(channelService.getChannelFromPeerTube(
                eq("channel_123"),
                eq(10), eq(2), isNull(), eq(0), eq(10), eq("id"), eq("asc")))
                .thenReturn(testChannel);

        doNothing().when(videoMinerService).saveChannel(testChannel);

        // Act & Assert
        mockMvc.perform(post("/api/channels/channel_123")
                .param("maxVideos", "10")
                .param("maxComments", "2"))
                .andExpect(status().isCreated());

        verify(channelService).getChannelFromPeerTube(
                eq("channel_123"),
                eq(10), eq(2), isNull(), eq(0), eq(10), eq("id"), eq("asc"));
        verify(videoMinerService).saveChannel(testChannel);
    }

    @Test
    @DisplayName("Debe retornar 404 al hacer POST de un canal inexistente")
    void testPostChannelNotFound() throws Exception {
        // Arrange
        when(channelService.getChannelFromPeerTube(
                eq("missing_channel"),
                anyInt(), anyInt(), isNull(), anyInt(), anyInt(), anyString(), anyString()))
                .thenThrow(new ChannelNotFoundException());

        // Act & Assert
        mockMvc.perform(post("/api/channels/missing_channel")
                .param("maxVideos", "10")
                .param("maxComments", "2"))
                .andExpect(status().isNotFound());

        verify(channelService).getChannelFromPeerTube(
                eq("missing_channel"),
                anyInt(), anyInt(), isNull(), anyInt(), anyInt(), anyString(), anyString());
        verify(videoMinerService, never()).saveChannel(any());
    }

    @Test
    @DisplayName("Debe respetar parámetros personalizados en POST")
    void testPostChannelWithCustomParameters() throws Exception {
        // Arrange
        when(channelService.getChannelFromPeerTube(
                eq("channel_123"),
                eq(20), eq(5), eq("filtered"), eq(2), eq(15), eq("name"), eq("desc")))
                .thenReturn(testChannel);

        doNothing().when(videoMinerService).saveChannel(testChannel);

        // Act & Assert
        mockMvc.perform(post("/api/channels/channel_123")
                .param("maxVideos", "20")
                .param("maxComments", "5")
                .param("name", "filtered")
                .param("page", "2")
                .param("size", "15")
                .param("sortBy", "name")
                .param("sortDir", "desc"))
                .andExpect(status().isCreated());

        verify(channelService).getChannelFromPeerTube(
                eq("channel_123"),
                eq(20), eq(5), eq("filtered"), eq(2), eq(15), eq("name"), eq("desc"));
        verify(videoMinerService).saveChannel(testChannel);
    }

    // ============ Tests de Validación ============

    @Test
    @DisplayName("Debe retornar 200 con parámetros válidos")
    void testGetChannelWithValidParameters() throws Exception {
        // Arrange
        when(channelService.getChannelFromPeerTube(
                eq("channel_123"),
                eq(10), eq(2), isNull(), eq(0), eq(10), eq("id"), eq("asc")))
                .thenReturn(testChannel);

        // Act & Assert
        mockMvc.perform(get("/api/channels/channel_123")
                .param("maxVideos", "10")
                .param("maxComments", "2"))
                .andExpect(status().isOk());

        verify(channelService).getChannelFromPeerTube(
                eq("channel_123"),
                eq(10), eq(2), isNull(), eq(0), eq(10), eq("id"), eq("asc"));
    }
}
