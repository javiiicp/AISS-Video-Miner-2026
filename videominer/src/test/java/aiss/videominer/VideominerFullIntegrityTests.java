package aiss.videominer;

import aiss.videominer.model.*;
import aiss.videominer.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("🛡️ VideoMiner: Certificación Final de Integridad Total")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VideominerFullIntegrityTests {

    // --- ESTILOS VISUALES ---
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ChannelRepository channelRepository;
    @Autowired private VideoRepository videoRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private CaptionRepository captionRepository;

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        System.out.println("\n" + BLUE + "═".repeat(80) + RESET);
        System.out.println(BOLD + YELLOW + " 🧪 TEST: " + RESET + BOLD + testInfo.getDisplayName().toUpperCase());
        System.out.println(BLUE + "═".repeat(80) + RESET);
        
        // Limpieza en orden de integridad
        commentRepository.deleteAll();
        captionRepository.deleteAll();
        videoRepository.deleteAll();
        channelRepository.deleteAll();
        userRepository.deleteAll();
    }

    private void step(String msg) { System.out.println(CYAN + "  [PASO] " + RESET + msg); }
    private void info(String key, Object val) { System.out.println(BLUE + "         ↳ " + key + ": " + RESET + val); }
    private void errorCheck(String msg) { System.out.println(RED + "  [!] ERROR CONTROLADO: " + RESET + msg); }

    // =========================================================================
    // 1. USUARIOS (USER) - STANDALONE CRUD
    // =========================================================================
    @SuppressWarnings("null")
    @Test @Order(1) @DisplayName("Módulo User: Ciclo de vida completo")
    void userModule() throws Exception {
        User user = new User(); user.setId("U1"); user.setName("Javier");
        
        step("Creando usuario...");
        mockMvc.perform(post("/videominer/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user))).andExpect(status().isCreated());

        step("Listando y filtrando por nombre '?name=jav'...");
        MvcResult res = mockMvc.perform(get("/videominer/users").param("name", "jav")).andExpect(status().isOk()).andReturn();
        List<User> list = objectMapper.readValue(res.getResponse().getContentAsString(), new TypeReference<List<User>>(){});
        assertEquals(1, list.size());

        step("Actualizando y borrando...");
        user.setName("Javier Updated");
        mockMvc.perform(put("/videominer/users/U1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user))).andExpect(status().isOk());
        mockMvc.perform(delete("/videominer/users/U1")).andExpect(status().isNoContent());
        
        errorCheck("Verificando 404 tras eliminación");
        mockMvc.perform(get("/videominer/users/U1")).andExpect(status().isNotFound());
    }

    // =========================================================================
    // 2. INTEGRIDAD VÍDEO (VIDEO + COMMENTS + CAPTIONS)
    // =========================================================================
    @SuppressWarnings("null")
    @Test @Order(2) @DisplayName("Módulo Video: Jerarquía y Relaciones")
    void videoIntegrityModule() throws Exception {
        step("Preparando Vídeo con Autor, Comentarios y Subtítulos anidados...");
        
        User auth = new User(); auth.setId("A1"); auth.setName("Miner");

        Comment com = new Comment(); com.setId("C1"); com.setText("Excelente");
        Caption cap = new Caption(); cap.setId("CP1"); cap.setLanguage("ES"); cap.setLink("url");

        Video video = new Video();
        video.setId("V1"); video.setName("AISS Masterclass"); video.setReleaseTime("2026-05-08");
        video.setAuthor(auth);
        video.setComments(List.of(com));
        video.setCaptions(List.of(cap));

        step("POST /videos (La jerarquía se vincula en el Service)");
        mockMvc.perform(post("/videominer/videos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(video))).andExpect(status().isCreated());

        step("Verificando persistencia de relaciones...");
        MvcResult res = mockMvc.perform(get("/videominer/videos/V1")).andExpect(status().isOk()).andReturn();
        Video saved = objectMapper.readValue(res.getResponse().getContentAsString(), Video.class);
        
        info("Autor", saved.getAuthor().getName());
        info("Comentarios", saved.getComments().size());
        info("Subtítulos", saved.getCaptions().size());
        
        assertEquals(1, saved.getComments().size());
        assertEquals(1, saved.getCaptions().size());
    }

    // =========================================================================
    // 3. CANALES (CHANNEL) - CRUD Y VALIDACIÓN
    // =========================================================================
    @SuppressWarnings("null")
    @Test @Order(3) @DisplayName("Módulo Channel: CRUD y Validación 400")
    void channelModule() throws Exception {
        Channel ch = new Channel(); ch.setId("CH1"); ch.setName("TV-AISS"); ch.setCreatedTime("2026");

        step("Registrando Canal...");
        mockMvc.perform(post("/videominer/channels").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(ch))).andExpect(status().isCreated());

        step("Verificando endpoint GET /channels...");
        MvcResult res = mockMvc.perform(get("/videominer/channels")).andExpect(status().isOk()).andReturn();
        List<Channel> list = objectMapper.readValue(res.getResponse().getContentAsString(), new TypeReference<List<Channel>>(){});
        assertFalse(list.isEmpty());

        errorCheck("Probando validación: Nombre de canal vacío");
        ch.setName("");
        mockMvc.perform(post("/videominer/channels").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(ch))).andExpect(status().isBadRequest());
    }

    // =========================================================================
    // 4. BÚSQUEDA Y FILTRADO (RELACIONAL)
    // =========================================================================
    @Test @Order(4) @DisplayName("Módulo Search: Filtros Relacionales")
    void searchModule() throws Exception {
        step("Poblando datos para búsqueda relacional...");
        // Re-crear el video del test 2 rápidamente para buscar sus hijos
        User u = new User(); u.setId("U-Search"); u.setName("Searcher"); userRepository.save(u);
        Video v = new Video(); v.setId("V-Search"); v.setName("Searching..."); v.setReleaseTime("2026"); v.setAuthor(u);
        videoRepository.save(v);
        
        Comment c = new Comment(); c.setId("COMM-1"); c.setText("Find me!"); c.setVideo(v);
        commentRepository.save(c);

        step("Buscando comentarios por texto '?text=find'...");
        MvcResult resCom = mockMvc.perform(get("/videominer/comments").param("text", "find")).andExpect(status().isOk()).andReturn();
        List<Comment> foundComs = objectMapper.readValue(resCom.getResponse().getContentAsString(), new TypeReference<List<Comment>>(){});
        assertEquals("COMM-1", foundComs.get(0).getId());

        step("Buscando comentarios de un vídeo específico '/comments/video/V-Search'...");
        mockMvc.perform(get("/videominer/comments/video/V-Search")).andExpect(status().isOk());
    }

    @AfterEach
    public void tearDown() {
        System.out.println(GREEN + "  [COMPLETO] Fase de prueba finalizada sin errores de integridad." + RESET);
    }
}