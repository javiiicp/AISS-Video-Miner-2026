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

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("VideoMiner: Certificación de Integridad Total")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VideominerIntegrationTests {

    // --- ESTILOS ANSI ---
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
        System.out.println("\n" + BLUE + "█".repeat(70) + RESET);
        System.out.println(BOLD + YELLOW + " 🧪 PROBANDO: " + RESET + BOLD + testInfo.getDisplayName());
        System.out.println(BLUE + "█".repeat(70) + RESET);
        
        commentRepository.deleteAll();
        captionRepository.deleteAll();
        videoRepository.deleteAll();
        channelRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        System.out.println(GREEN + "  [COMPLETO] Limpieza de entorno finalizada." + RESET);
    }

    private void step(String msg) { System.out.println(CYAN + "  [PASO] " + RESET + msg); }
    private void errorLog(String msg) { System.out.println(RED + "  [ERROR-TEST] " + RESET + msg); }

    // =========================================================================
    // 1. CANALES (CHANNEL) - 6 OPERACIONES
    // =========================================================================
    @Test @Order(1) @DisplayName("Módulo Channel: CRUD completo y filtrado")
    void channelModule() throws Exception {
        Channel ch = new Channel(); ch.setId("C1"); ch.setName("TV-AISS"); ch.setCreatedTime("2024"); ch.setVideos(new ArrayList<>());
        
        step("1. POST: Creando canal 'TV-AISS'");
        mockMvc.perform(post("/videominer/channels").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(ch))).andExpect(status().isCreated());
        
        step("2. GET ALL: Listando canales");
        mockMvc.perform(get("/videominer/channels")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        
        step("3. GET BY ID: Buscando C1");
        mockMvc.perform(get("/videominer/channels/C1")).andExpect(status().isOk());
        
        step("4. FILTER: Buscando '?name=aiss'");
        mockMvc.perform(get("/videominer/channels").param("name", "aiss")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name", containsString("AISS")));
        
        step("5. PUT: Actualizando nombre");
        ch.setName("TV-UPDATED");
        mockMvc.perform(put("/videominer/channels/C1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(ch))).andExpect(status().isOk());
        
        step("6. DELETE: Borrando canal");
        mockMvc.perform(delete("/videominer/channels/C1")).andExpect(status().isNoContent());
        
        errorLog("Verificando error 404 tras borrado");
        mockMvc.perform(get("/videominer/channels/C1")).andExpect(status().isNotFound());
    }

    // =========================================================================
    // 2. VÍDEOS (VIDEO) - 6 OPERACIONES
    // =========================================================================
    @Test @Order(2) @DisplayName("Módulo Video: CRUD, Autoría y Relación de Subtítulos")
    void videoModule() throws Exception {
        User u = new User(); u.setId("U1"); u.setName("Javi"); userRepository.save(u);
        Video v = new Video(); v.setId("V1"); v.setName("Learn Spring"); v.setReleaseTime("2024"); v.setAuthor(u);
        
        step("1. POST: Creando vídeo con autor");
        mockMvc.perform(post("/videominer/videos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(v))).andExpect(status().isCreated());
        
        step("2. GET ALL: Listando vídeos");
        mockMvc.perform(get("/videominer/videos")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        
        step("3. GET BY ID: Buscando V1");
        mockMvc.perform(get("/videominer/videos/V1")).andExpect(status().isOk()).andExpect(jsonPath("$.author.name").value("Javi"));
        
        step("4. FILTER: Buscando por nombre '?name=spring'");
        mockMvc.perform(get("/videominer/videos").param("name", "spring")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name", containsString("Spring")));
        
        step("5. GET RELATIONAL: Obteniendo subtítulos del vídeo");
        mockMvc.perform(get("/videominer/videos/V1/captions")).andExpect(status().isOk());
        
        errorLog("Probando error 400: POST vídeo sin autor");
        v.setAuthor(null);
        mockMvc.perform(post("/videominer/videos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(v))).andExpect(status().isBadRequest());
    }

    // =========================================================================
    // 3. USUARIOS (USER) - 6 OPERACIONES
    // =========================================================================
    @Test @Order(3) @DisplayName("Módulo User: CRUD y Búsqueda")
    void userModule() throws Exception {
        User u = new User(); u.setId("USR"); u.setName("Campos");
        
        step("1. POST: Creando usuario");
        mockMvc.perform(post("/videominer/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(u))).andExpect(status().isCreated());
        
        step("2. GET ALL: Listando usuarios");
        mockMvc.perform(get("/videominer/users")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        
        step("3. GET BY ID: Buscando USR");
        mockMvc.perform(get("/videominer/users/USR")).andExpect(status().isOk());
        
        step("4. FILTER: Buscando '?name=cam'");
        mockMvc.perform(get("/videominer/users").param("name", "cam")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name", containsString("Campos")));
        
        step("5. PUT: Cambiando enlaces");
        u.setUser_link("http://link");
        mockMvc.perform(put("/videominer/users/USR").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(u))).andExpect(status().isOk());
        
        step("6. DELETE: Borrando usuario");
        mockMvc.perform(delete("/videominer/users/USR")).andExpect(status().isNoContent());
    }

    // =========================================================================
    // 4. COMENTARIOS (COMMENT) - 6 OPERACIONES
    // =========================================================================
    @Test @Order(4) @DisplayName("Módulo Comment: CRUD y Filtro por Vídeo")
    void commentModule() throws Exception {
        User u = new User(); u.setId("U2"); u.setName("U2"); userRepository.save(u);
        Video v = new Video(); v.setId("V2"); v.setName("V2"); v.setReleaseTime("2024"); v.setAuthor(u); videoRepository.save(v);
        Comment com = new Comment(); com.setId("CM1"); com.setText("Awesome!"); com.setVideo(v);
        
        step("1. POST: Creando comentario para V2");
        mockMvc.perform(post("/videominer/comments").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(com))).andExpect(status().isCreated());
        
        step("2. GET ALL: Todos los comentarios");
        mockMvc.perform(get("/videominer/comments")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        
        step("3. GET BY VIDEO: Comentarios del vídeo V2");
        mockMvc.perform(get("/videominer/comments/video/V2")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value("CM1"));
        
        step("4. FILTER: Buscando texto '?text=awe'");
        mockMvc.perform(get("/videominer/comments").param("text", "awe")).andExpect(status().isOk()).andExpect(jsonPath("$[0].text").value("Awesome!"));
        
        step("5. PUT: Actualizando texto");
        com.setText("Cool!");
        mockMvc.perform(put("/videominer/comments/CM1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(com))).andExpect(status().isOk());
        
        errorLog("Probando error 404: Borrar comentario inexistente");
        mockMvc.perform(delete("/videominer/comments/999")).andExpect(status().isNotFound());
    }

    // =========================================================================
    // 5. SUBTÍTULOS (CAPTION) - 6 OPERACIONES
    // =========================================================================
    @Test @Order(5) @DisplayName("Módulo Caption: CRUD y Verificación de Idioma")
    void captionModule() throws Exception {
        User u = new User(); u.setId("U3"); u.setName("U3"); userRepository.save(u);
        Video v = new Video(); v.setId("V3"); v.setName("V3"); v.setReleaseTime("2024"); v.setAuthor(u); videoRepository.save(v);
        Caption cp = new Caption(); cp.setId("CP1"); cp.setLanguage("English"); cp.setLink("url"); cp.setVideo(v);
        
        step("1. POST: Guardando caption para V3");
        mockMvc.perform(post("/videominer/captions").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(cp))).andExpect(status().isCreated());
        
        step("2. GET ALL: Listando captions");
        mockMvc.perform(get("/videominer/captions")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        
        step("3. GET BY ID: Buscando CP1");
        mockMvc.perform(get("/videominer/captions/CP1")).andExpect(status().isOk());
        
        step("4. GET BY VIDEO: Captions de V3");
        mockMvc.perform(get("/videominer/captions/video/V3")).andExpect(status().isOk()).andExpect(jsonPath("$[0].language").value("English"));
        
        step("5. PUT: Actualizando idioma");
        cp.setLanguage("Spanish");
        mockMvc.perform(put("/videominer/captions/CP1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(cp))).andExpect(status().isOk());
        
        step("6. DELETE: Borrando subtítulo");
        mockMvc.perform(delete("/videominer/captions/CP1")).andExpect(status().isNoContent());
    }
}