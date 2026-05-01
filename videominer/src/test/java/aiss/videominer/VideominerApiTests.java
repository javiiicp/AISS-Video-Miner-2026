package aiss.videominer;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import aiss.videominer.model.Video;
import aiss.videominer.repository.CaptionRepository;
import aiss.videominer.repository.ChannelRepository;
import aiss.videominer.repository.CommentRepository;
import aiss.videominer.repository.UserRepository;
import aiss.videominer.repository.VideoRepository;

/**
 * Suite de tests de integración para VideoMiner API.
 * Prueba todos los endpoints CRUD y validaciones.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("VideoMiner API Integration Tests")
class VideominerApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private CaptionRepository captionRepository;

    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private ChannelRepository channelRepository;
    
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        commentRepository.deleteAll();
        captionRepository.deleteAll();
        videoRepository.deleteAll();
        channelRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldReturn404WhenVideoDoesNotExist() throws Exception {
      cleanDatabase();
        mockMvc.perform(get("/videominer/videos/missing-video"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateAndReadCaptionByVideo() throws Exception {
      cleanDatabase();
        Video video = new Video();
        video.setId("video-1");
        video.setName("Sample video");
        video.setDescription("Sample description");
        video.setReleaseTime("2026-04-30T10:00:00Z");
        video.setComments(new ArrayList<>());
        video.setCaptions(new ArrayList<>());
        videoRepository.save(video);

        mockMvc.perform(post("/videominer/captions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "caption-1",
                                  "link": "https://example.com/caption.vtt",
                                  "language": "es",
                                  "video": { "id": "video-1" }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("caption-1"))
                .andExpect(jsonPath("$.video.id").value("video-1"));

        mockMvc.perform(get("/videominer/captions/video/video-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("caption-1"));
    }

    @Test
    void shouldCreateCommentAndRejectMissingVideoOnLookup() throws Exception {
      cleanDatabase();
        Video video = new Video();
        video.setId("video-2");
        video.setName("Another video");
        video.setDescription("Another description");
        video.setReleaseTime("2026-04-30T10:00:00Z");
        video.setComments(new ArrayList<>());
        video.setCaptions(new ArrayList<>());
        videoRepository.save(video);

        mockMvc.perform(post("/videominer/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "comment-1",
                                  "text": "Nice video",
                                  "createdOn": "2026-04-30T11:00:00Z",
                                  "video": { "id": "video-2" }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("comment-1"))
                .andExpect(jsonPath("$.video.id").value("video-2"));

        mockMvc.perform(get("/videominer/comments/video/missing-video"))
                .andExpect(status().isNotFound());
    }

          @Test
          void shouldCreateUpdateAndDeleteChannel() throws Exception {
        cleanDatabase();

        mockMvc.perform(post("/videominer/channels")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
              {
                "id": "channel-1",
                "name": "My Channel",
                "description": "Main channel",
                "createdTime": "2026-04-30T10:00:00Z",
                "videos": []
              }
              """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("channel-1"));

        mockMvc.perform(put("/videominer/channels/channel-1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
              {
                "id": "other-id",
                "name": "My Channel Updated",
                "description": "Updated",
                "createdTime": "2026-04-30T10:30:00Z",
                "videos": []
              }
              """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("channel-1"))
          .andExpect(jsonPath("$.name").value("My Channel Updated"));

        mockMvc.perform(delete("/videominer/channels/channel-1"))
          .andExpect(status().isNoContent());

        mockMvc.perform(delete("/videominer/channels/channel-1"))
          .andExpect(status().isNotFound());
          }

          @Test
          void shouldCreateUpdateAndDeleteVideo() throws Exception {
        cleanDatabase();

        mockMvc.perform(post("/videominer/videos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
              {
                "id": "video-crud-1",
                "name": "Video CRUD",
                "description": "Desc",
                "releaseTime": "2026-04-30T12:00:00Z",
                "comments": [],
                "captions": []
              }
              """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("video-crud-1"));

        mockMvc.perform(put("/videominer/videos/video-crud-1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
              {
                "id": "another-id",
                "name": "Video CRUD Updated",
                "description": "Desc Updated",
                "releaseTime": "2026-04-30T13:00:00Z",
                "comments": [],
                "captions": []
              }
              """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("video-crud-1"))
          .andExpect(jsonPath("$.name").value("Video CRUD Updated"));

        mockMvc.perform(delete("/videominer/videos/video-crud-1"))
          .andExpect(status().isNoContent());

        mockMvc.perform(delete("/videominer/videos/video-crud-1"))
          .andExpect(status().isNotFound());
          }

          @Test
          void shouldReturnValidationErrorForInvalidVideoPayload() throws Exception {
        cleanDatabase();

        mockMvc.perform(post("/videominer/videos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
              {
                "id": "invalid-video",
                "name": "",
                "description": "Desc",
                "releaseTime": "",
                "comments": [],
                "captions": []
              }
              """))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400))
          .andExpect(jsonPath("$.fieldErrors.name").exists())
          .andExpect(jsonPath("$.fieldErrors.releaseTime").exists());
          }

          @Test
          void shouldReturn404WhenUpdatingOrDeletingMissingVideo() throws Exception {
        cleanDatabase();

        mockMvc.perform(put("/videominer/videos/missing")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
              {
                "id": "video-x",
                "name": "Any",
                "description": "Any",
                "releaseTime": "2026-04-30T10:00:00Z",
                "comments": [],
                "captions": []
              }
              """))
          .andExpect(status().isNotFound());

        mockMvc.perform(delete("/videominer/videos/missing"))
          .andExpect(status().isNotFound());
    }

    // ======== TESTS DE USUARIOS ========

    @Test
    @DisplayName("CRUD Usuarios: Crear, obtener, actualizar y eliminar")
    void shouldCreateUpdateAndDeleteUser() throws Exception {
        String jsonUser = """
            {
              "id": "user-1",
              "name": "Juan Pérez",
              "user_link": "https://example.com/juan",
              "picture_link": "https://example.com/images/juan.jpg"
            }
            """;

        // CREATE - POST /users (201 Created)
        mockMvc.perform(post("/videominer/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("user-1"))
            .andExpect(jsonPath("$.name").value("Juan Pérez"));

        // READ - GET /users/{id} (200 OK)
        mockMvc.perform(get("/videominer/users/user-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("user-1"));

        // UPDATE - PUT /users/{id} (200 OK)
        String jsonUserUpd = """
            {
              "id": "different-id",
              "name": "Juan Pérez Updated",
              "user_link": "https://example.com/juan-new",
              "picture_link": "https://example.com/juan2.jpg"
            }
            """;
        mockMvc.perform(put("/videominer/users/user-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUserUpd))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Juan Pérez Updated"));

        // DELETE - DELETE /users/{id} (204 No Content)
        mockMvc.perform(delete("/videominer/users/user-1"))
            .andExpect(status().isNoContent());

        // VERIFY DELETED - GET /users/{id} (404 Not Found)
        mockMvc.perform(get("/videominer/users/user-1"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Validación: Usuario sin nombre debe fallar")
    void shouldValidateRequiredFieldsInUser() throws Exception {
        String invalidUser = """
            {
              "id": "user-invalid",
              "name": "",
              "user_link": "https://example.com/user"
            }
            """;

        mockMvc.perform(post("/videominer/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidUser))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    @DisplayName("Búsqueda: Listar usuarios con filtro por nombre")
    void shouldSearchUsersByName() throws Exception {
        // Crear 2 usuarios
        mockMvc.perform(post("/videominer/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "id": "user-alice",
                      "name": "Alice Smith"
                    }
                    """))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/videominer/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "id": "user-bob",
                      "name": "Bob Johnson"
                    }
                    """))
            .andExpect(status().isCreated());

        // Buscar por nombre "Alice"
        mockMvc.perform(get("/videominer/users?name=alice"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Alice Smith"))
            .andExpect(jsonPath("$.totalElements").value(1));

        // Buscar por nombre "Bob"
        mockMvc.perform(get("/videominer/users?name=bob"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Bob Johnson"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    // ======== TESTS DE CAPTIONS (SUBTÍTULOS) ========

    @Test
    @DisplayName("CRUD Captions: Crear, obtener, actualizar y eliminar")
    void shouldCreateUpdateAndDeleteCaption() throws Exception {
        // Crear video primero
        Video video = new Video();
        video.setId("video-for-captions");
        video.setName("Video with Captions");
        video.setDescription("Test video");
        video.setReleaseTime("2026-04-30T10:00:00Z");
        video.setComments(new ArrayList<>());
        video.setCaptions(new ArrayList<>());
        videoRepository.save(video);

        // CREATE - POST /captions (201 Created)
        String jsonCaption = """
            {
              "id": "caption-es",
              "link": "https://example.com/subtitles.es.vtt",
              "language": "es",
              "video": { "id": "video-for-captions" }
            }
            """;
        mockMvc.perform(post("/videominer/captions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCaption))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("caption-es"))
            .andExpect(jsonPath("$.language").value("es"));

        // UPDATE - PUT /captions/{id} (200 OK)
        String jsonCaptionUpd = """
            {
              "id": "caption-id-ignored",
              "link": "https://example.com/subtitles-updated.vtt",
              "language": "es-MX",
              "video": { "id": "video-for-captions" }
            }
            """;
        mockMvc.perform(put("/videominer/captions/caption-es")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCaptionUpd))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.language").value("es-MX"));

        // DELETE - DELETE /captions/{id} (204 No Content)
        mockMvc.perform(delete("/videominer/captions/caption-es"))
            .andExpect(status().isNoContent());

        // VERIFY DELETED - GET /captions/{id} (404 Not Found)
        mockMvc.perform(get("/videominer/captions/caption-es"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Validación: Caption sin vídeo debe fallar")
    void shouldValidateRequiredFieldsInCaption() throws Exception {
        String invalidCaption = """
            {
              "id": "caption-invalid",
              "link": "https://example.com/sub.vtt",
              "language": "en"
            }
            """;

        mockMvc.perform(post("/videominer/captions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidCaption))
            .andExpect(status().isBadRequest());
    }

    // ======== TESTS DE COMMENTS ========

    @Test
    @DisplayName("CRUD Comments: Crear, obtener, actualizar y eliminar")
    void shouldCreateUpdateAndDeleteComment() throws Exception {
        // Crear video primero
        Video video = new Video();
        video.setId("video-for-comments");
        video.setName("Video with Comments");
        video.setDescription("Test video");
        video.setReleaseTime("2026-04-30T10:00:00Z");
        video.setComments(new ArrayList<>());
        video.setCaptions(new ArrayList<>());
        videoRepository.save(video);

        // CREATE - POST /comments (201 Created)
        String jsonComment = """
            {
              "id": "comment-1",
              "text": "¡Excelente vídeo, muy instructivo!",
              "createdOn": "2026-04-30T11:00:00Z",
              "video": { "id": "video-for-comments" }
            }
            """;
        mockMvc.perform(post("/videominer/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonComment))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("comment-1"))
            .andExpect(jsonPath("$.text").value("¡Excelente vídeo, muy instructivo!"));

        // UPDATE - PUT /comments/{id} (200 OK)
        String jsonCommentUpd = """
            {
              "id": "comment-id-ignored",
              "text": "Texto actualizado",
              "createdOn": "2026-04-30T12:00:00Z",
              "video": { "id": "video-for-comments" }
            }
            """;
        mockMvc.perform(put("/videominer/comments/comment-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCommentUpd))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.text").value("Texto actualizado"));

        // DELETE - DELETE /comments/{id} (204 No Content)
        mockMvc.perform(delete("/videominer/comments/comment-1"))
            .andExpect(status().isNoContent());

        // VERIFY DELETED - GET /comments/{id} (404 Not Found)
        mockMvc.perform(get("/videominer/comments/comment-1"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Validación: Comment sin vídeo debe fallar")
    void shouldValidateRequiredFieldsInComment() throws Exception {
        String invalidComment = """
            {
              "id": "comment-invalid",
              "text": "Comment without video",
              "createdOn": "2026-04-30T11:00:00Z"
            }
            """;

        mockMvc.perform(post("/videominer/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidComment))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Búsqueda: Buscar comentarios por texto")
    void shouldSearchCommentsByText() throws Exception {
        // Crear video
        Video video = new Video();
        video.setId("video-search");
        video.setName("Video for Comment Search");
        video.setDescription("Test");
        video.setReleaseTime("2026-04-30T10:00:00Z");
        video.setComments(new ArrayList<>());
        video.setCaptions(new ArrayList<>());
        videoRepository.save(video);

        // Crear 2 comentarios con diferente texto
        mockMvc.perform(post("/videominer/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "id": "comment-search-1",
                      "text": "Excelente contenido",
                      "createdOn": "2026-04-30T11:00:00Z",
                      "video": { "id": "video-search" }
                    }
                    """))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/videominer/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "id": "comment-search-2",
                      "text": "Muy malo, no recomiendo",
                      "createdOn": "2026-04-30T12:00:00Z",
                      "video": { "id": "video-search" }
                    }
                    """))
            .andExpect(status().isCreated());

        // Buscar por "excelente"
        mockMvc.perform(get("/videominer/comments?text=excelente"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].text").value("Excelente contenido"))
            .andExpect(jsonPath("$.totalElements").value(1));

        // Buscar por "malo"
        mockMvc.perform(get("/videominer/comments?text=malo"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].text").value("Muy malo, no recomiendo"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    // ======== TESTS DE BÚSQUEDA Y PAGINACIÓN ========

    @Test
    @DisplayName("Búsqueda: Listar canales con filtro por nombre")
    void shouldSearchChannelsByName() throws Exception {
        // Crear 2 canales
        mockMvc.perform(post("/videominer/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "id": "channel-tech",
                      "name": "Technology Channel",
                      "description": "Tech content",
                      "createdTime": "2026-01-01T10:00:00Z",
                      "videos": []
                    }
                    """))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/videominer/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "id": "channel-fitness",
                      "name": "Fitness Channel",
                      "description": "Fitness content",
                      "createdTime": "2026-01-02T10:00:00Z",
                      "videos": []
                    }
                    """))
            .andExpect(status().isCreated());

        // Buscar "Technology"
        mockMvc.perform(get("/videominer/channels?name=technology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Technology Channel"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Búsqueda: Listar vídeos con filtro por nombre")
    void shouldSearchVideosByName() throws Exception {
        // Crear 2 videos
        mockMvc.perform(post("/videominer/videos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "id": "video-java-tutorial",
                      "name": "Java Tutorial for Beginners",
                      "description": "Learn Java",
                      "releaseTime": "2026-03-01T10:00:00Z",
                      "comments": [],
                      "captions": []
                    }
                    """))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/videominer/videos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "id": "video-python-basics",
                      "name": "Python Basics Course",
                      "description": "Learn Python",
                      "releaseTime": "2026-03-02T10:00:00Z",
                      "comments": [],
                      "captions": []
                    }
                    """))
            .andExpect(status().isCreated());

        // Buscar "Java"
        mockMvc.perform(get("/videominer/videos?name=java"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Java Tutorial for Beginners"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Paginación: Verificar que paginación funciona correctamente")
    void shouldVerifyPaginationWorks() throws Exception {
        // Crear 15 videos para probar paginación
        for (int i = 1; i <= 15; i++) {
            mockMvc.perform(post("/videominer/videos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(String.format("""
                        {
                          "id": "video-page-%d",
                          "name": "Video Page %d",
                          "description": "Desc",
                          "releaseTime": "2026-03-0%dT10:00:00Z",
                          "comments": [],
                          "captions": []
                        }
                        """, i, i, (i % 9) + 1)))
                .andExpect(status().isCreated());
        }

        // Página 1 (10 elementos por defecto)
        mockMvc.perform(get("/videominer/videos?page=0&size=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(10))
            .andExpect(jsonPath("$.totalElements").value(15))
            .andExpect(jsonPath("$.totalPages").value(2));

        // Página 2
        mockMvc.perform(get("/videominer/videos?page=1&size=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(5));

        // Tamaño 20 (todos en una página)
        mockMvc.perform(get("/videominer/videos?page=0&size=20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(15))
            .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("Relaciones: Obtener captions de un vídeo")
    void shouldGetCaptionsByVideo() throws Exception {
        // Crear video
        Video video = new Video();
        video.setId("video-multi-captions");
        video.setName("Video Multi Captions");
        video.setDescription("Test");
        video.setReleaseTime("2026-04-30T10:00:00Z");
        video.setComments(new ArrayList<>());
        video.setCaptions(new ArrayList<>());
        videoRepository.save(video);

        // Crear 3 captions
        for (String lang : new String[]{"es", "en", "fr"}) {
            mockMvc.perform(post("/videominer/captions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(String.format("""
                        {
                          "id": "caption-%s",
                          "link": "https://example.com/subtitles.%s.vtt",
                          "language": "%s",
                          "video": { "id": "video-multi-captions" }
                        }
                        """, lang, lang, lang)))
                .andExpect(status().isCreated());
        }

        // Obtener captions del video vía endpoint especial
        mockMvc.perform(get("/videominer/videos/video-multi-captions/captions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(3))
            .andExpect(jsonPath("$.totalElements").value(3));
    }

    @Test
    @DisplayName("Relaciones: Obtener comments de un vídeo")
    void shouldGetCommentsByVideo() throws Exception {
        // Crear video
        Video video = new Video();
        video.setId("video-multi-comments");
        video.setName("Video Multi Comments");
        video.setDescription("Test");
        video.setReleaseTime("2026-04-30T10:00:00Z");
        video.setComments(new ArrayList<>());
        video.setCaptions(new ArrayList<>());
        videoRepository.save(video);

        // Crear 2 comments
        for (int i = 1; i <= 2; i++) {
            mockMvc.perform(post("/videominer/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(String.format("""
                        {
                          "id": "comment-video-%d",
                          "text": "Comment %d",
                          "createdOn": "2026-04-30T1%d:00:00Z",
                          "video": { "id": "video-multi-comments" }
                        }
                        """, i, i, i)))
                .andExpect(status().isCreated());
        }

        // Obtener comments del video
        mockMvc.perform(get("/videominer/comments/video/video-multi-comments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("Estado HTTP: Verificar códigos de estado correctos")
    void shouldReturnCorrectStatusCodes() throws Exception {
        // 201 Created
        mockMvc.perform(post("/videominer/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "id": "channel-status",
                      "name": "Status Test Channel",
                      "createdTime": "2026-01-01T10:00:00Z",
                      "videos": []
                    }
                    """))
            .andExpect(status().isCreated());

        // 200 OK (GET)
        mockMvc.perform(get("/videominer/channels/channel-status"))
            .andExpect(status().isOk());

        // 200 OK (PUT)
        mockMvc.perform(put("/videominer/channels/channel-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "id": "ignored",
                      "name": "Updated Channel",
                      "createdTime": "2026-01-01T10:00:00Z",
                      "videos": []
                    }
                    """))
            .andExpect(status().isOk());

        // 204 No Content (DELETE)
        mockMvc.perform(delete("/videominer/channels/channel-status"))
            .andExpect(status().isNoContent());

        // 404 Not Found
        mockMvc.perform(get("/videominer/channels/missing-channel"))
            .andExpect(status().isNotFound());

        // 400 Bad Request (validación)
        mockMvc.perform(post("/videominer/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "id": "channel-invalid",
                      "name": "",
                      "createdTime": ""
                    }
                    """))
            .andExpect(status().isBadRequest());
    }
}