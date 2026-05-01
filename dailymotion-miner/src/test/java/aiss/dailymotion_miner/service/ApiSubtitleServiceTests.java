package aiss.dailymotion_miner.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import aiss.dailymotion_miner.model.Caption;

/**
 * Pruebas unitarias para ApiSubtitleService
 * Valida la obtención y procesamiento de subtítulos desde la API de Dailymotion
 * 
 * @author Test Suite
 */
@ExtendWith(MockitoExtension.class)
class ApiSubtitleServiceTests {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ApiSubtitleService apiSubtitleService;

    private String testVideoId;

    @BeforeEach
    void setUp() {
        testVideoId = "test_video_123";
    }

    // ============ Pruebas de obtención de subtítulos correcta ============

    @Test
    void testGetSubtitlesByVideoIdSuccess() {
        // Arrange
        String videoId = "video_xyz";
        String url = "https://api.dailymotion.com/video/" + videoId + "/subtitles";

        List<Map<String, Object>> mockSubtitleList = new ArrayList<>();
        Map<String, Object> subtitle1 = new HashMap<>();
        subtitle1.put("id", "sub_1");
        subtitle1.put("link", "http://example.com/sub1.vtt");
        subtitle1.put("language", "es");
        mockSubtitleList.add(subtitle1);

        when(restTemplate.getForObject(url, Object.class)).thenReturn(mockSubtitleList);

        // Act
        List<Caption> result = apiSubtitleService.getSubtitlesByVideoId(videoId);

        // Assert
        assertNotNull(result, "El resultado no debe ser null");
        assertEquals(1, result.size(), "Debe haber un subtítulo");
        verify(restTemplate).getForObject(url, Object.class);
    }

    @Test
    void testGetSubtitlesByVideoIdMultipleLanguages() {
        // Arrange
        String videoId = "video_multi";
        String url = "https://api.dailymotion.com/video/" + videoId + "/subtitles";

        List<Map<String, Object>> mockSubtitleList = new ArrayList<>();
        Map<String, Object> subtitleEs = new HashMap<>();
        subtitleEs.put("id", "sub_es");
        subtitleEs.put("language", "es");
        mockSubtitleList.add(subtitleEs);

        Map<String, Object> subtitleEn = new HashMap<>();
        subtitleEn.put("id", "sub_en");
        subtitleEn.put("language", "en");
        mockSubtitleList.add(subtitleEn);

        Map<String, Object> subtitleFr = new HashMap<>();
        subtitleFr.put("id", "sub_fr");
        subtitleFr.put("language", "fr");
        mockSubtitleList.add(subtitleFr);

        when(restTemplate.getForObject(url, Object.class)).thenReturn(mockSubtitleList);

        // Act
        List<Caption> result = apiSubtitleService.getSubtitlesByVideoId(videoId);

        // Assert
        assertNotNull(result, "El resultado no debe ser null");
        assertEquals(3, result.size(), "Debe haber tres subtítulos");
    }

    // ============ Pruebas de respuesta nula ============

    @Test
    void testGetSubtitlesByVideoIdNullResponse() {
        // Arrange
        String videoId = "video_null";
        String url = "https://api.dailymotion.com/video/" + videoId + "/subtitles";

        when(restTemplate.getForObject(url, Object.class)).thenReturn(null);

        // Act
        List<Caption> result = apiSubtitleService.getSubtitlesByVideoId(videoId);

        // Assert
        assertNotNull(result, "El resultado debe ser una lista vacía, no null");
        assertTrue(result.isEmpty(), "La lista debe estar vacía cuando la respuesta es null");
    }

    @Test
    void testGetSubtitlesByVideoIdEmptyList() {
        // Arrange
        String videoId = "video_empty";
        String url = "https://api.dailymotion.com/video/" + videoId + "/subtitles";

        when(restTemplate.getForObject(url, Object.class)).thenReturn(new ArrayList<>());

        // Act
        List<Caption> result = apiSubtitleService.getSubtitlesByVideoId(videoId);

        // Assert
        assertNotNull(result, "El resultado no debe ser null");
        assertTrue(result.isEmpty(), "La lista debe estar vacía");
    }

    // ============ Pruebas de manejo de excepciones ============

    @Test
    void testGetSubtitlesByVideoIdException() {
        // Arrange
        String videoId = "video_error";
        String url = "https://api.dailymotion.com/video/" + videoId + "/subtitles";

        when(restTemplate.getForObject(url, Object.class))
            .thenThrow(new RuntimeException("Connection error"));

        // Act
        List<Caption> result = apiSubtitleService.getSubtitlesByVideoId(videoId);

        // Assert
        assertNotNull(result, "El resultado debe ser una lista vacía en caso de error");
        assertTrue(result.isEmpty(), "La lista debe estar vacía cuando hay una excepción");
    }

    // ============ Pruebas de parseSubtitles (invocado indirectamente) ============

    @Test
    void testSubtitleParsingWithCompleteData() {
        // Arrange
        String videoId = "video_complete";
        String url = "https://api.dailymotion.com/video/" + videoId + "/subtitles";

        List<Map<String, Object>> mockSubtitleList = new ArrayList<>();
        Map<String, Object> subtitle = new HashMap<>();
        subtitle.put("id", "sub_complete");
        subtitle.put("link", "https://api.dailymotion.com/subtitles/abc123.vtt");
        subtitle.put("language", "es");
        mockSubtitleList.add(subtitle);

        when(restTemplate.getForObject(url, Object.class)).thenReturn(mockSubtitleList);

        // Act
        List<Caption> result = apiSubtitleService.getSubtitlesByVideoId(videoId);

        // Assert
        assertEquals(1, result.size(), "Debe haber una caption");
        Caption caption = result.get(0);
        assertNotNull(caption.getId(), "El caption debe tener un ID");
        assertNotNull(caption.getLanguage(), "El caption debe tener un lenguaje");
    }

    @Test
    void testSubtitleParsingWithPartialData() {
        // Arrange
        String videoId = "video_partial";
        String url = "https://api.dailymotion.com/video/" + videoId + "/subtitles";

        List<Map<String, Object>> mockSubtitleList = new ArrayList<>();
        Map<String, Object> subtitle = new HashMap<>();
        subtitle.put("id", "sub_partial");
        // Sin 'link'
        // Sin 'language'
        mockSubtitleList.add(subtitle);

        when(restTemplate.getForObject(url, Object.class)).thenReturn(mockSubtitleList);

        // Act
        List<Caption> result = apiSubtitleService.getSubtitlesByVideoId(videoId);

        // Assert
        assertEquals(1, result.size(), "Debe haber una caption incluso con datos parciales");
        Caption caption = result.get(0);
        assertNotNull(caption, "La caption no debe ser null");
    }

    @Test
    void testSubtitleParsingWithNullValues() {
        // Arrange
        String videoId = "video_null_values";
        String url = "https://api.dailymotion.com/video/" + videoId + "/subtitles";

        List<Map<String, Object>> mockSubtitleList = new ArrayList<>();
        Map<String, Object> subtitle = new HashMap<>();
        subtitle.put("id", null);
        subtitle.put("link", null);
        subtitle.put("language", null);
        mockSubtitleList.add(subtitle);

        when(restTemplate.getForObject(url, Object.class)).thenReturn(mockSubtitleList);

        // Act
        List<Caption> result = apiSubtitleService.getSubtitlesByVideoId(videoId);

        // Assert
        assertEquals(1, result.size(), "Debe procesar captions incluso con valores null");
    }

    // ============ Pruebas de validación de URL ============

    @Test
    void testURLConstructionCorrect() {
        // Arrange
        String videoId = "test_video_id";
        String expectedUrl = "https://api.dailymotion.com/video/test_video_id/subtitles";

        when(restTemplate.getForObject(expectedUrl, Object.class))
            .thenReturn(new ArrayList<>());

        // Act
        apiSubtitleService.getSubtitlesByVideoId(videoId);

        // Assert
        verify(restTemplate).getForObject(expectedUrl, Object.class);
    }

    @Test
    void testURLWithSpecialCharacters() {
        // Arrange
        String videoId = "video_with-special_chars@123";
        String expectedUrl = "https://api.dailymotion.com/video/" + videoId + "/subtitles";

        when(restTemplate.getForObject(expectedUrl, Object.class))
            .thenReturn(new ArrayList<>());

        // Act
        apiSubtitleService.getSubtitlesByVideoId(videoId);

        // Assert
        verify(restTemplate).getForObject(expectedUrl, Object.class);
    }

    // ============ Pruebas de manejo de tipos de datos ============

    @Test
    void testParsingWithNonMapElements() {
        // Arrange
        String videoId = "video_non_map";
        String url = "https://api.dailymotion.com/video/" + videoId + "/subtitles";

        List<Object> mixedList = new ArrayList<>();
        mixedList.add("string_element"); // No es un Map
        Map<String, Object> validSubtitle = new HashMap<>();
        validSubtitle.put("id", "valid_sub");
        validSubtitle.put("language", "en");
        mixedList.add(validSubtitle); // Es un Map válido

        when(restTemplate.getForObject(url, Object.class)).thenReturn(mixedList);

        // Act
        List<Caption> result = apiSubtitleService.getSubtitlesByVideoId(videoId);

        // Assert
        assertNotNull(result, "El resultado no debe ser null");
        // La cantidad exacta depende de cómo el parseSubtitles maneje los elementos no-Map
    }

    @Test
    void testParsingWithResponseNotAList() {
        // Arrange
        String videoId = "video_not_list";
        String url = "https://api.dailymotion.com/video/" + videoId + "/subtitles";

        Map<String, Object> response = new HashMap<>();
        response.put("id", "response_id");
        response.put("data", new ArrayList<>());

        when(restTemplate.getForObject(url, Object.class)).thenReturn(response);

        // Act
        List<Caption> result = apiSubtitleService.getSubtitlesByVideoId(videoId);

        // Assert
        assertNotNull(result, "El resultado debe ser una lista vacía");
        assertTrue(result.isEmpty(), "Si la respuesta no es lista, retorna lista vacía");
    }

    // ============ Pruebas de casos extremos ============

    @Test
    void testGetSubtitlesWithEmptyVideoId() {
        // Arrange
        String videoId = "";
        String url = "https://api.dailymotion.com/video//subtitles";

        when(restTemplate.getForObject(url, Object.class))
            .thenThrow(new RuntimeException("Invalid URL"));

        // Act
        List<Caption> result = apiSubtitleService.getSubtitlesByVideoId(videoId);

        // Assert
        assertNotNull(result, "El resultado debe ser una lista vacía incluso con ID vacío");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetSubtitlesWithLargeNumberOfSubtitles() {
        // Arrange
        String videoId = "video_many";
        String url = "https://api.dailymotion.com/video/" + videoId + "/subtitles";

        List<Map<String, Object>> largeSubtitleList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Map<String, Object> subtitle = new HashMap<>();
            subtitle.put("id", "sub_" + i);
            subtitle.put("language", "lang_" + i);
            largeSubtitleList.add(subtitle);
        }

        when(restTemplate.getForObject(url, Object.class)).thenReturn(largeSubtitleList);

        // Act
        List<Caption> result = apiSubtitleService.getSubtitlesByVideoId(videoId);

        // Assert
        assertNotNull(result, "El resultado no debe ser null");
        assertEquals(100, result.size(), "Debe procesar todos los subtítulos");
    }

    // ============ Pruebas de integración de comportamiento ============

    @Test
    void testMultipleCallsWithDifferentVideoIds() {
        // Arrange
        String url1 = "https://api.dailymotion.com/video/video1/subtitles";
        String url2 = "https://api.dailymotion.com/video/video2/subtitles";

        List<Map<String, Object>> list1 = new ArrayList<>();
        Map<String, Object> sub1 = new HashMap<>();
        sub1.put("id", "sub_1");
        list1.add(sub1);

        List<Map<String, Object>> list2 = new ArrayList<>();
        Map<String, Object> sub2 = new HashMap<>();
        sub2.put("id", "sub_2");
        list2.add(sub2);

        when(restTemplate.getForObject(url1, Object.class)).thenReturn(list1);
        when(restTemplate.getForObject(url2, Object.class)).thenReturn(list2);

        // Act
        List<Caption> result1 = apiSubtitleService.getSubtitlesByVideoId("video1");
        List<Caption> result2 = apiSubtitleService.getSubtitlesByVideoId("video2");

        // Assert
        assertEquals(1, result1.size());
        assertEquals(1, result2.size());
        verify(restTemplate, times(2)).getForObject(anyString(), eq(Object.class));
    }
}
