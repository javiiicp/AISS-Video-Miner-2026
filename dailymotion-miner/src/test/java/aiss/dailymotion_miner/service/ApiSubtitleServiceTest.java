package aiss.dailymotion_miner.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;

import aiss.dailymotion_miner.model.Caption;
import aiss.dailymotion_miner.model.external.DailymotionSubtitle;
import aiss.dailymotion_miner.model.external.DailymotionSubtitleSearch;

/**
 * Test unitario para ApiSubtitleService
 * Prueba que el mapeo JSON a objetos DailymotionSubtitle funciona correctamente
 */
@SpringJUnitConfig
@DisplayName("ApiSubtitleService Tests")
class ApiSubtitleServiceTest {

    @MockBean
    private RestTemplate restTemplate;
    
    private ApiSubtitleService subtitleService;

    @BeforeEach
    void setUp() {
        subtitleService = new ApiSubtitleService();
        // Inyectar manualmente el mock de RestTemplate
        org.springframework.test.util.ReflectionTestUtils.setField(
            subtitleService, "restTemplate", restTemplate
        );
    }

    @Test
    @DisplayName("Debe mapear correctamente subtítulos desde JSON")
    void testGetSubtitlesMapping() {
        // Preparar datos de prueba que simulan la respuesta de la API
        DailymotionSubtitle sub1 = new DailymotionSubtitle();
        sub1.setId("sub_001");
        sub1.setLanguage("en");
        sub1.setLanguageLabel("English");
        sub1.setFormat("vtt");
        sub1.setItemType("subtitle");
        sub1.setUrl("https://api.dailymotion.com/subtitles/en.vtt");

        DailymotionSubtitle sub2 = new DailymotionSubtitle();
        sub2.setId("sub_002");
        sub2.setLanguage("es");
        sub2.setLanguageLabel("Spanish");
        sub2.setFormat("vtt");
        sub2.setItemType("subtitle");
        sub2.setUrl("https://api.dailymotion.com/subtitles/es.vtt");

        DailymotionSubtitleSearch response = new DailymotionSubtitleSearch();
        response.setSubtitles(Arrays.asList(sub1, sub2));

        // Mockear la respuesta de RestTemplate
        String videoId = "test_video_123";
        when(restTemplate.getForObject(
            "https://api.dailymotion.com/video/" + videoId + "/subtitles",
            DailymotionSubtitleSearch.class
        )).thenReturn(response);

        // Ejecutar el servicio
        List<Caption> result = subtitleService.getSubtitles(videoId);

        // Verificar que se mapeó correctamente
        assertNotNull(result, "La lista de captions no debe ser null");
        assertEquals(2, result.size(), "Debería haber 2 captions");
        
        // Verificar primer subtítulo
        Caption caption1 = result.get(0);
        assertEquals("sub_001", caption1.getId(), "ID del primer caption");
        assertEquals("en", caption1.getLanguage(), "Idioma del primer caption");
        assertEquals("https://api.dailymotion.com/subtitles/en.vtt", caption1.getLink(), "URL del primer caption");
        
        // Verificar segundo subtítulo
        Caption caption2 = result.get(1);
        assertEquals("sub_002", caption2.getId(), "ID del segundo caption");
        assertEquals("es", caption2.getLanguage(), "Idioma del segundo caption");
        assertEquals("https://api.dailymotion.com/subtitles/es.vtt", caption2.getLink(), "URL del segundo caption");
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando no hay subtítulos")
    void testGetSubtitlesEmpty() {
        DailymotionSubtitleSearch response = new DailymotionSubtitleSearch();
        response.setSubtitles(null);

        String videoId = "video_sin_subtitulos";
        when(restTemplate.getForObject(
            "https://api.dailymotion.com/video/" + videoId + "/subtitles",
            DailymotionSubtitleSearch.class
        )).thenReturn(response);

        List<Caption> result = subtitleService.getSubtitles(videoId);

        assertNotNull(result, "La lista no debe ser null");
        assertEquals(0, result.size(), "Debería estar vacía");
    }

    @Test
    @DisplayName("Debe manejar excepciones de la API")
    void testGetSubtitlesException() {
        String videoId = "video_error";
        when(restTemplate.getForObject(
            "https://api.dailymotion.com/video/" + videoId + "/subtitles",
            DailymotionSubtitleSearch.class
        )).thenThrow(new RuntimeException("Error de conexión a la API"));

        List<Caption> result = subtitleService.getSubtitles(videoId);

        assertNotNull(result, "La lista no debe ser null incluso con error");
        assertEquals(0, result.size(), "Debería estar vacía en caso de error");
    }

}
