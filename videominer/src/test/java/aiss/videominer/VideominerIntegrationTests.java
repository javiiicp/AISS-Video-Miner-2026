package aiss.videominer;

import aiss.videominer.model.*;
import aiss.videominer.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("VideoMiner: Suite de Integridad Total")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VideominerIntegrationTests {

    // --- UTILIDADES VISUALES ---
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ChannelRepository channelRepository;
    @Autowired private VideoRepository videoRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private CaptionRepository captionRepository;

    @BeforeEach
    void setup(TestInfo testInfo) {
        System.out.println("\n" + BLUE + "═".repeat(80) + RESET);
        System.out.println(BOLD + PURPLE + " 🔎 TEST: " + RESET + BOLD + testInfo.getDisplayName().toUpperCase());
        System.out.println(BLUE + "═".repeat(80) + RESET);
        
        commentRepository.deleteAll();
        captionRepository.deleteAll();
        videoRepository.deleteAll();
        channelRepository.deleteAll();
        userRepository.deleteAll();
    }

    private void step(String msg) { System.out.println(YELLOW + "  [ACCION] " + RESET + msg); }
    private void info(String key, Object val) { System.out.println(CYAN + "           ↳ " + key + ": " + RESET + val); }
    private void success(String msg) { System.out.println(GREEN + "  [PASSED] " + RESET + msg); }
    private void errorCheck(String msg) { System.out.println(RED + "  [ERROR-CONTROL] " + RESET + msg); }

    // ==========================================
    // 1. GESTIÓN DE USUARIOS (USER)
    // ==========================================

    @Test @Order(1) @DisplayName("User: CRUD Completo y Búsqueda por Nombre")
    void testUserModule() throws Exception {
        User user = new User();
        user.setId("U1"); user.setName("Javier");
        
        step("Probando POST /users (Creación)");
        mockMvc.perform(post("/videominer/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user))).andExpect(status().isCreated());
        
        step("Probando GET /users?name=jav (Filtrado)");
        mockMvc.perform(get("/videominer/users").param("name", "jav")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        
        step("Probando PUT /users/U1 (Actualización)");
        user.setName("Javier Pro");
        mockMvc.perform(put("/videominer/users/U1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user))).andExpect(status().isOk());
        
        errorCheck("Forzando 404 en GET /users/999");
        mockMvc.perform(get("/videominer/users/999")).andExpect(status().isNotFound());
        
        success("Módulo de Usuarios validado al 100%");
    }

    // ==========================================
    // 2. GESTIÓN DE CANALES (CHANNEL)
    // ==========================================

    @Test @Order(2) @DisplayName("Channel: CRUD y Búsqueda por Nombre")
    void testChannelModule() throws Exception {
        Channel channel = new Channel();
        channel.setId("CH1"); channel.setName("Canal AISS"); channel.setCreatedTime("2026-01-01");
        
        step("Probando POST /channels");
        mockMvc.perform(post("/videominer/channels").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(channel))).andExpect(status().isCreated());
        
        step("Probando GET /channels (Listado plano)");
        mockMvc.perform(get("/videominer/channels")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));

        errorCheck("Probando Validación 400 (Nombre vacío)");
        channel.setName("");
        mockMvc.perform(post("/videominer/channels").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(channel))).andExpect(status().isBadRequest());
        
        success("Módulo de Canales validado");
    }

    // ==========================================
    // 3. GESTIÓN DE VÍDEOS (VIDEO)
    // ==========================================

    @Test @Order(3) @DisplayName("Video: CRUD, Autoría y Relación de Captions")
    void testVideoModule() throws Exception {
        User author = new User(); author.setId("A1"); author.setName("Author 1");
        userRepository.save(author);

        Video video = new Video();
        video.setId("V1"); video.setName("Intro Java"); video.setReleaseTime("2026"); video.setAuthor(author);

        step("Probando POST /videos con Autor");
        mockMvc.perform(post("/videominer/videos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(video))).andExpect(status().isCreated());

        step("Probando GET /videos/V1/captions (Relación vacía)");
        mockMvc.perform(get("/videominer/videos/V1/captions")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));

        errorCheck("Probando 400: Vídeo sin autor");
        video.setAuthor(null);
        mockMvc.perform(post("/videominer/videos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(video))).andExpect(status().isBadRequest());

        success("Módulo de Vídeos validado");
    }

    // ==========================================
    // 4. GESTIÓN DE SUBTÍTULOS (CAPTION)
    // ==========================================

    @Test @Order(4) @DisplayName("Caption: CRUD y Filtro por Vídeo")
    void testCaptionModule() throws Exception {
        // Setup
        User author = new User(); author.setId("A2"); author.setName("A2"); userRepository.save(author);
        Video video = new Video(); video.setId("V2"); video.setName("V2"); video.setReleaseTime("2026"); video.setAuthor(author); videoRepository.save(video);

        Caption cap = new Caption();
        cap.setId("C1"); cap.setLanguage("ES"); cap.setLink("url"); cap.setVideo(video);

        step("Probando POST /captions vinculado a V2");
        mockMvc.perform(post("/videominer/captions").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(cap))).andExpect(status().isCreated());

        step("Probando GET /captions/video/V2 (Filtro relacional)");
        mockMvc.perform(get("/videominer/captions/video/V2")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));

        errorCheck("Probando 404: GET /captions/video/MISSING");
        mockMvc.perform(get("/videominer/captions/video/MISSING")).andExpect(status().isNotFound());

        success("Módulo de Subtítulos validado");
    }

    // ==========================================
    // 5. GESTIÓN DE COMENTARIOS (COMMENT)
    // ==========================================

    @Test @Order(5) @DisplayName("Comment: CRUD y Filtro por Texto")
    void testCommentModule() throws Exception {
        User author = new User(); author.setId("A3"); author.setName("A3"); userRepository.save(author);
        Video video = new Video(); video.setId("V3"); video.setName("V3"); video.setReleaseTime("2026"); video.setAuthor(author); videoRepository.save(video);

        Comment com = new Comment();
        com.setId("CM1"); com.setText("Excelente video"); com.setVideo(video);

        step("Probando POST /comments vinculado a V3");
        mockMvc.perform(post("/videominer/comments").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(com))).andExpect(status().isCreated());

        step("Probando GET /comments?text=excel (Filtrado de texto)");
        mockMvc.perform(get("/videominer/comments").param("text", "excel")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));

        errorCheck("Probando 204: DELETE /comments/CM1");
        mockMvc.perform(delete("/videominer/comments/CM1")).andExpect(status().isNoContent());

        success("Módulo de Comentarios validado");
    }

    @AfterEach
    void tearDown() {
        System.out.println(CYAN + "  [FIN] Pruebas del módulo finalizadas con éxito." + RESET);
    }
}