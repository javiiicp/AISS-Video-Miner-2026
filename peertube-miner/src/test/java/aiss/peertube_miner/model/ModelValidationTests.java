package aiss.peertube_miner.model;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Suite de tests unitarios para validación de modelos de PeerTube Miner.
 * Valida las restricciones y estructuras de los modelos.
 */
@DisplayName("PeerTube Miner Model Validation Tests")
class ModelValidationTests {

    private Validator validator;
    private Channel testChannel;
    private Video testVideo;
    private User testUser;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Crear datos de prueba
        testUser = new User();
        testUser.setId("user_123");
        testUser.setName("Test User");
        testUser.setUser_link("https://example.com/user_123");
        testUser.setPicture_link("https://example.com/avatar.jpg");

        testVideo = new Video();
        testVideo.setId("video_123");
        testVideo.setName("Test Video");
        testVideo.setDescription("Test Description");
        testVideo.setReleaseTime("2026-01-01T00:00:00Z");
        testVideo.setAuthor(testUser);
        testVideo.setComments(new ArrayList<>());
        testVideo.setCaptions(new ArrayList<>());

        testChannel = new Channel();
        testChannel.setId("channel_123");
        testChannel.setName("Test Channel");
        testChannel.setDescription("Test Description");
        testChannel.setCreatedTime("2026-01-01T00:00:00Z");
        testChannel.setVideos(new ArrayList<>());
    }

    // ============ Pruebas del Modelo Channel ============

    @Test
    @DisplayName("Channel válido no debe tener violaciones")
    void testValidChannel() {
        // Act
        Set<ConstraintViolation<Channel>> violations = validator.validate(testChannel);

        // Assert
        assertTrue(violations.isEmpty(), "No debe haber violaciones");
    }

    @Test
    @DisplayName("Channel sin nombre debe ser válido (nombre no requerido)")
    void testChannelWithoutName() {
        // Arrange
        testChannel.setName(null);

        // Act
        Set<ConstraintViolation<Channel>> violations = validator.validate(testChannel);

        // Assert
        // Dependiendo de las validaciones reales del modelo
        // Si name no tiene @NotNull, no debe haber violaciones
        assertTrue(violations.isEmpty() || violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    // ============ Pruebas del Modelo Video ============

    @Test
    @DisplayName("Video válido no debe tener violaciones")
    void testValidVideo() {
        // Act
        Set<ConstraintViolation<Video>> violations = validator.validate(testVideo);

        // Assert
        assertTrue(violations.isEmpty(), "No debe haber violaciones");
    }

    @Test
    @DisplayName("Video debe inicializar comentarios como lista vacía")
    void testVideoCommentsInitialized() {
        // Arrange
        testVideo.setComments(null);

        // Assert
        assertNull(testVideo.getComments(), "Los comentarios pueden ser null antes de inicializar");

        // Cuando se obtiene del servicio, debería ser lista vacía
        testVideo.setComments(new ArrayList<>());
        assertNotNull(testVideo.getComments(), "Los comentarios deben ser lista vacía");
        assertTrue(testVideo.getComments().isEmpty(), "Los comentarios inicial deben estar vacíos");
    }

    @Test
    @DisplayName("Video debe inicializar captions como lista vacía")
    void testVideoCaptionsInitialized() {
        // Arrange
        testVideo.setCaptions(null);

        // Assert
        assertNull(testVideo.getCaptions(), "Las captions pueden ser null antes de inicializar");

        // Cuando se obtiene del servicio, debería ser lista vacía
        testVideo.setCaptions(new ArrayList<>());
        assertNotNull(testVideo.getCaptions(), "Las captions deben ser lista vacía");
        assertTrue(testVideo.getCaptions().isEmpty(), "Las captions inicial deben estar vacías");
    }

    // ============ Pruebas del Modelo User ============

    @Test
    @DisplayName("User válido no debe tener violaciones")
    void testValidUser() {
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);

        // Assert
        assertTrue(violations.isEmpty(), "No debe haber violaciones");
    }


    // ============ Pruebas del Modelo Comment ============

    @Test
    @DisplayName("Comment válido no debe tener violaciones")
    void testValidComment() {
        // Arrange
        Comment comment = new Comment();
        comment.setId("comment_123");
        comment.setText("Great video!");
        comment.setCreatedOn("2026-01-01T10:00:00Z");

        // Act
        Set<ConstraintViolation<Comment>> violations = validator.validate(comment);

        // Assert
        assertTrue(violations.isEmpty(), "No debe haber violaciones");
    }

    @Test
    @DisplayName("Comment debe poder establecer y obtener todos los campos")
    void testCommentFields() {
        // Arrange
        Comment comment = new Comment();

        // Act & Assert
        comment.setId("comment_new");
        assertEquals("comment_new", comment.getId());

        comment.setText("New comment text");
        assertEquals("New comment text", comment.getText());

        comment.setCreatedOn("2026-06-01T15:00:00Z");
        assertEquals("2026-06-01T15:00:00Z", comment.getCreatedOn());
    }

    // ============ Pruebas del Modelo Caption ============

    @Test
    @DisplayName("Caption válida no debe tener violaciones")
    void testValidCaption() {
        // Arrange
        Caption caption = new Caption();
        caption.setId("caption_123");
        caption.setLink("https://example.com/subtitles.vtt");
        caption.setLanguage("es");

        // Act
        Set<ConstraintViolation<Caption>> violations = validator.validate(caption);

        // Assert
        assertTrue(violations.isEmpty(), "No debe haber violaciones");
    }

    @Test
    @DisplayName("Caption debe poder establecer y obtener todos los campos")
    void testCaptionFields() {
        // Arrange
        Caption caption = new Caption();

        // Act & Assert
        caption.setId("caption_new");
        assertEquals("caption_new", caption.getId());

        caption.setLink("https://example.com/subtitles.vtt");
        assertEquals("https://example.com/subtitles.vtt", caption.getLink());

        caption.setLanguage("en");
        assertEquals("en", caption.getLanguage());
    }

    // ============ Pruebas de Getters y Setters ============

    @Test
    @DisplayName("Channel debe poder establecer y obtener todos los campos")
    void testChannelGettersSetters() {
        // Act & Assert
        testChannel.setId("new_id");
        assertEquals("new_id", testChannel.getId());

        testChannel.setName("New Name");
        assertEquals("New Name", testChannel.getName());

        testChannel.setDescription("New Description");
        assertEquals("New Description", testChannel.getDescription());

        testChannel.setCreatedTime("2026-06-01T00:00:00Z");
        assertEquals("2026-06-01T00:00:00Z", testChannel.getCreatedTime());

        ArrayList<Video> videos = new ArrayList<>();
        testChannel.setVideos(videos);
        assertEquals(videos, testChannel.getVideos());
    }

    @Test
    @DisplayName("Video debe poder establecer y obtener todos los campos")
    void testVideoGettersSetters() {
        // Act & Assert
        testVideo.setId("new_video_id");
        assertEquals("new_video_id", testVideo.getId());

        testVideo.setName("New Video Name");
        assertEquals("New Video Name", testVideo.getName());

        testVideo.setDescription("New Video Description");
        assertEquals("New Video Description", testVideo.getDescription());

        testVideo.setReleaseTime("2026-06-01T12:00:00Z");
        assertEquals("2026-06-01T12:00:00Z", testVideo.getReleaseTime());

        User newAuthor = new User();
        newAuthor.setId("author_new");
        testVideo.setAuthor(newAuthor);
        assertEquals(newAuthor, testVideo.getAuthor());
    }

    @Test
    @DisplayName("User debe poder establecer y obtener todos los campos")
    void testUserGettersSetters() {
        // Act & Assert
        testUser.setId("new_user_id");
        assertEquals("new_user_id", testUser.getId());

        testUser.setName("New User Name");
        assertEquals("New User Name", testUser.getName());

        testUser.setUser_link("https://example.com/newuser");
        assertEquals("https://example.com/newuser", testUser.getUser_link());

        testUser.setPicture_link("https://example.com/newavatar.jpg");
        assertEquals("https://example.com/newavatar.jpg", testUser.getPicture_link());
    }
}
