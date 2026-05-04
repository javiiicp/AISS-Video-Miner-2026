package aiss.dailymotion_miner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import aiss.dailymotion_miner.model.Channel;
import aiss.dailymotion_miner.service.ApiChannelService;
import aiss.dailymotion_miner.service.VideominerService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

/**
 * Suite de tests de integración para Dailymotion Miner API.
 * Prueba los endpoints GET/POST del controlador de canales.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Dailymotion Miner API Integration Tests")
class DailymotionMinerApiTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiChannelService channelService;

    @MockBean
    private VideominerService videominerService;

    private Channel testChannel;

    @BeforeEach
    void setUp() {
        // Crear un canal de prueba
        testChannel = new Channel();
        testChannel.setId("dailymotion_channel_123");
        testChannel.setName("Dailymotion Test Channel");
        testChannel.setDescription("Test Description");
        testChannel.setCreatedTime("2026-01-01T00:00:00Z");
        testChannel.setVideos(new ArrayList<>());
    }

    // ============ Tests del Endpoint GET ============

    @Test
    @DisplayName("Debe retornar 200 al obtener un canal válido")
    void testGetChannelWithValidId() throws Exception {
        // Arrange
        when(channelService.getChannelFromDailymotion(
                eq("dailymotion_channel_123"),
                anyInt(), anyInt()))
                .thenReturn(testChannel);

        // Act & Assert
        mockMvc.perform(get("/api/channels/dailymotion_channel_123")
                .param("maxVideos", "10")
                .param("maxPages", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("dailymotion_channel_123"))
                .andExpect(jsonPath("$.name").value("Dailymotion Test Channel"))
                .andExpect(jsonPath("$.description").value("Test Description"));

        verify(channelService).getChannelFromDailymotion(
                eq("dailymotion_channel_123"),
                eq(10), eq(2));
    }

    @Test
    @DisplayName("Debe retornar 404 cuando el canal no existe")
    void testGetChannelNotFound() throws Exception {
        // Arrange
        when(channelService.getChannelFromDailymotion(
                eq("missing_channel"),
                anyInt(), anyInt()))
                .thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/channels/missing_channel")
                .param("maxVideos", "10")
                .param("maxPages", "2"))
                .andExpect(status().isNotFound());

        verify(channelService).getChannelFromDailymotion(
                eq("missing_channel"),
                anyInt(), anyInt());
    }

    @Test
    @DisplayName("Debe respetar parámetros maxVideos y maxPages en GET")
    void testGetChannelWithCustomLimits() throws Exception {
        // Arrange
        when(channelService.getChannelFromDailymotion(
                eq("dailymotion_channel_123"),
                eq(5), eq(3)))
                .thenReturn(testChannel);

        // Act & Assert
        mockMvc.perform(get("/api/channels/dailymotion_channel_123")
                .param("maxVideos", "5")
                .param("maxPages", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("dailymotion_channel_123"));

        verify(channelService).getChannelFromDailymotion(
                eq("dailymotion_channel_123"),
                eq(5), eq(3));
    }

    @Test
    @DisplayName("Debe usar valores por defecto cuando no se especifican parámetros")
    void testGetChannelWithDefaultParameters() throws Exception {
        // Arrange
        when(channelService.getChannelFromDailymotion(
                eq("dailymotion_channel_123"),
                eq(10), eq(2)))
                .thenReturn(testChannel);

        // Act & Assert
        mockMvc.perform(get("/api/channels/dailymotion_channel_123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("dailymotion_channel_123"));

        verify(channelService).getChannelFromDailymotion(
                eq("dailymotion_channel_123"),
                eq(10), eq(2));
    }

    // ============ Tests del Endpoint POST ============

    @Test
    @DisplayName("Debe retornar 201 al guardar un canal exitosamente")
    void testPostChannelSuccessfully() throws Exception {
        // Arrange
        when(channelService.getChannelFromDailymotion(
                eq("dailymotion_channel_123"),
                eq(10), eq(2)))
                .thenReturn(testChannel);

        when(videominerService.saveChannel(testChannel)).thenReturn(testChannel);

        // Act & Assert
        mockMvc.perform(post("/api/channels/dailymotion_channel_123")
                .param("maxVideos", "10")
                .param("maxPages", "2"))
                .andExpect(status().isCreated());

        verify(channelService).getChannelFromDailymotion(
                eq("dailymotion_channel_123"),
                eq(10), eq(2));
        verify(videominerService).saveChannel(testChannel);
    }

    @Test
    @DisplayName("Debe retornar 404 al hacer POST de un canal inexistente")
    void testPostChannelNotFound() throws Exception {
        // Arrange
        when(channelService.getChannelFromDailymotion(
                eq("missing_channel"),
                anyInt(), anyInt()))
                .thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/channels/missing_channel")
                .param("maxVideos", "10")
                .param("maxPages", "2"))
                .andExpect(status().isNotFound());

        verify(channelService).getChannelFromDailymotion(
                eq("missing_channel"),
                anyInt(), anyInt());
        verify(videominerService, never()).saveChannel(any());
    }

    @Test
    @DisplayName("Debe respetar parámetros personalizados en POST")
    void testPostChannelWithCustomParameters() throws Exception {
        // Arrange
        when(channelService.getChannelFromDailymotion(
                eq("dailymotion_channel_123"),
                eq(20), eq(5)))
                .thenReturn(testChannel);

        when(videominerService.saveChannel(testChannel)).thenReturn(testChannel);

        // Act & Assert
        mockMvc.perform(post("/api/channels/dailymotion_channel_123")
                .param("maxVideos", "20")
                .param("maxPages", "5"))
                .andExpect(status().isCreated());

        verify(channelService).getChannelFromDailymotion(
                eq("dailymotion_channel_123"),
                eq(20), eq(5));
        verify(videominerService).saveChannel(testChannel);
    }

    @Test
    @DisplayName("Debe aceptar parámetros numéricos válidos")
    void testGetChannelValidNumericParameters() throws Exception {
        // Arrange
        when(channelService.getChannelFromDailymotion(
                eq("dailymotion_channel_123"),
                eq(100), eq(50)))
                .thenReturn(testChannel);

        // Act & Assert
        mockMvc.perform(get("/api/channels/dailymotion_channel_123")
                .param("maxVideos", "100")
                .param("maxPages", "50"))
                .andExpect(status().isOk());

        verify(channelService).getChannelFromDailymotion(
                eq("dailymotion_channel_123"),
                eq(100), eq(50));
    }
}
