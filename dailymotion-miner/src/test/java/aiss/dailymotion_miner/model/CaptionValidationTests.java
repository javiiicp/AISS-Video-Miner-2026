package aiss.dailymotion_miner.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Pruebas unitarias para la validación de la clase Caption
 * 
 * @author Test Suite
 */
class CaptionValidationTests {

    private Validator validator;
    private Video mockVideo;

    @BeforeEach
    void setUp() {
        // Inicializar el validador
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        // Crear un video mock para las pruebas
        mockVideo = new Video();
        mockVideo.setId("video_123");
        mockVideo.setName("Test Video");
    }

    // ============ Pruebas de validación @NotNull en video ============

    @Test
    void testCaptionVideoNotNull() {
        // Arrange
        Caption caption = new Caption();
        caption.setId("caption_1");
        caption.setLink("http://example.com/caption.vtt");
        caption.setLanguage("es");
        caption.setVideo(mockVideo);

        // Act
        Set<ConstraintViolation<Caption>> violations = validator.validate(caption);

        // Assert
        assertTrue(violations.isEmpty(), "No debe haber violaciones de validación");
    }

    @Test
    void testCaptionVideoNull() {
        // Arrange
        Caption caption = new Caption();
        caption.setId("caption_1");
        caption.setLink("http://example.com/caption.vtt");
        caption.setLanguage("es");
        caption.setVideo(null);

        // Act
        Set<ConstraintViolation<Caption>> violations = validator.validate(caption);

        // Assert
        assertFalse(violations.isEmpty(), "Debe haber una violación por video null");
        assertTrue(
            violations.stream()
                .anyMatch(v -> v.getMessage().contains("Caption must be associated with a video")),
            "El mensaje de error debe indicar que la caption debe asociarse con un video"
        );
    }

    // ============ Pruebas de Getters y Setters ============

    @Test
    void testSetAndGetId() {
        // Arrange
        Caption caption = new Caption();
        String testId = "caption_123";

        // Act
        caption.setId(testId);
        String result = caption.getId();

        // Assert
        assertEquals(testId, result, "El ID debe ser igual al asignado");
    }

    @Test
    void testSetAndGetLink() {
        // Arrange
        Caption caption = new Caption();
        String testLink = "https://api.example.com/subtitles/file.vtt";

        // Act
        caption.setLink(testLink);
        String result = caption.getLink();

        // Assert
        assertEquals(testLink, result, "El link debe ser igual al asignado");
    }

    @Test
    void testSetAndGetLanguage() {
        // Arrange
        Caption caption = new Caption();
        String testLanguage = "en";

        // Act
        caption.setLanguage(testLanguage);
        String result = caption.getLanguage();

        // Assert
        assertEquals(testLanguage, result, "El lenguaje debe ser igual al asignado");
    }

    @Test
    void testSetAndGetVideo() {
        // Arrange
        Caption caption = new Caption();
        Video testVideo = new Video();
        testVideo.setId("video_456");

        // Act
        caption.setVideo(testVideo);
        Video result = caption.getVideo();

        // Assert
        assertEquals(testVideo, result, "El video debe ser igual al asignado");
        assertEquals("video_456", result.getId(), "El ID del video debe ser correcto");
    }

    // ============ Pruebas de valores nulos en campos opcionales ============

    @Test
    void testCaptionWithNullLink() {
        // Arrange
        Caption caption = new Caption();
        caption.setId("caption_1");
        caption.setLink(null);
        caption.setLanguage("es");
        caption.setVideo(mockVideo);

        // Act
        Set<ConstraintViolation<Caption>> violations = validator.validate(caption);

        // Assert
        assertTrue(violations.isEmpty(), "Los campos opcionales pueden ser null");
        assertNull(caption.getLink(), "El link debe ser null");
    }

    @Test
    void testCaptionWithNullLanguage() {
        // Arrange
        Caption caption = new Caption();
        caption.setId("caption_1");
        caption.setLink("http://example.com/caption.vtt");
        caption.setLanguage(null);
        caption.setVideo(mockVideo);

        // Act
        Set<ConstraintViolation<Caption>> violations = validator.validate(caption);

        // Assert
        assertTrue(violations.isEmpty(), "Los campos opcionales pueden ser null");
        assertNull(caption.getLanguage(), "El language debe ser null");
    }

    @Test
    void testCaptionWithEmptyFields() {
        // Arrange
        Caption caption = new Caption();
        caption.setId("");
        caption.setLink("");
        caption.setLanguage("");
        caption.setVideo(mockVideo);

        // Act
        Set<ConstraintViolation<Caption>> violations = validator.validate(caption);

        // Assert
        assertTrue(violations.isEmpty(), "Los campos vacíos son válidos (no hay restricciones de longitud)");
    }

    // ============ Pruebas de comportamiento de la entidad ============

    @Test
    void testCaptionToString() {
        // Arrange
        Caption caption = new Caption();
        caption.setId("caption_123");
        caption.setLink("https://example.com/sub.vtt");
        caption.setLanguage("es");
        caption.setVideo(mockVideo);

        // Act
        String result = caption.toString();

        // Assert
        assertNotNull(result, "El toString no debe retornar null");
        assertTrue(result.contains("caption_123"), "El toString debe contener el ID");
        assertTrue(result.contains("https://example.com/sub.vtt"), "El toString debe contener el link");
        assertTrue(result.contains("es"), "El toString debe contener el lenguaje");
        assertTrue(result.startsWith("Caption{"), "El toString debe empezar con Caption{");
    }

    // ============ Pruebas de construcción con múltiples idiomas ============

    @Test
    void testCaptionWithDifferentLanguages() {
        // Arrange
        String[] languages = { "es", "en", "fr", "de", "pt", "ja", "zh" };

        for (String lang : languages) {
            // Act
            Caption caption = new Caption();
            caption.setId("caption_" + lang);
            caption.setLanguage(lang);
            caption.setVideo(mockVideo);

            Set<ConstraintViolation<Caption>> violations = validator.validate(caption);

            // Assert
            assertTrue(violations.isEmpty(), 
                "La caption con lenguaje " + lang + " debe ser válida");
            assertEquals(lang, caption.getLanguage(), 
                "El lenguaje debe ser " + lang);
        }
    }

    // ============ Pruebas de casos extremos ============

    @Test
    void testCaptionWithVeryLongValues() {
        // Arrange
        Caption caption = new Caption();
        String longId = "a".repeat(1000);
        String longLink = "https://example.com/" + "a".repeat(500);
        String longLanguage = "a".repeat(100);

        // Act
        caption.setId(longId);
        caption.setLink(longLink);
        caption.setLanguage(longLanguage);
        caption.setVideo(mockVideo);
        Set<ConstraintViolation<Caption>> violations = validator.validate(caption);

        // Assert
        assertTrue(violations.isEmpty(), 
            "Los valores largos no deben causar violaciones de validación");
        assertEquals(1000, caption.getId().length());
        assertEquals(longLanguage, caption.getLanguage());
    }

    @Test
    void testCaptionWithSpecialCharacters() {
        // Arrange
        Caption caption = new Caption();
        caption.setId("caption_@#$%");
        caption.setLink("https://example.com/sub?lang=es&id=123");
        caption.setLanguage("zh-CN");
        caption.setVideo(mockVideo);

        // Act
        Set<ConstraintViolation<Caption>> violations = validator.validate(caption);

        // Assert
        assertTrue(violations.isEmpty(), 
            "Los caracteres especiales no deben causar violaciones");
        assertEquals("zh-CN", caption.getLanguage());
    }

    // ============ Pruebas de referencias y comparación ============

    @Test
    void testCaptionMultipleInstancesWithSameVideo() {
        // Arrange
        Caption caption1 = new Caption();
        caption1.setId("caption_1");
        caption1.setLanguage("es");
        caption1.setVideo(mockVideo);

        Caption caption2 = new Caption();
        caption2.setId("caption_2");
        caption2.setLanguage("en");
        caption2.setVideo(mockVideo);

        // Act
        Set<ConstraintViolation<Caption>> violations1 = validator.validate(caption1);
        Set<ConstraintViolation<Caption>> violations2 = validator.validate(caption2);

        // Assert
        assertTrue(violations1.isEmpty() && violations2.isEmpty(), 
            "Múltiples captions pueden compartir el mismo video");
        assertSame(caption1.getVideo(), caption2.getVideo(), 
            "Ambas captions deben referenciar el mismo objeto Video");
    }

    @Test
    void testCaptionVideoReferenceUpdate() {
        // Arrange
        Caption caption = new Caption();
        caption.setId("caption_1");
        caption.setVideo(mockVideo);
        Video initialVideo = caption.getVideo();

        // Act
        Video newVideo = new Video();
        newVideo.setId("video_new");
        caption.setVideo(newVideo);
        Video updatedVideo = caption.getVideo();

        // Assert
        assertNotEquals(initialVideo.getId(), updatedVideo.getId(), 
            "La referencia del video debe haber sido actualizada");
        assertEquals("video_new", caption.getVideo().getId());
    }
}
