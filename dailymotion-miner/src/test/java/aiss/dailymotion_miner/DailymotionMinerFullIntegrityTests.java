package aiss.dailymotion_miner;

import static org.hamcrest.CoreMatchers.containsString;

import aiss.dailymotion_miner.model.Channel;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("🚀 Dailymotion Miner: Certificación de Integridad y ETL")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DailymotionMinerFullIntegrityTests {

    // --- ESTILOS VISUALES ANSI ---
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";

    @Autowired private MockMvc mockMvc;
    @Autowired private RestTemplate restTemplate;
    @Autowired private ObjectMapper objectMapper;

    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        System.out.println("\n" + PURPLE + "█".repeat(80) + RESET);
        System.out.println(BOLD + YELLOW + " 🛰️ PROBANDO DAILYMOTION: " + RESET + BOLD + testInfo.getDisplayName().toUpperCase());
        System.out.println(PURPLE + "█".repeat(80) + RESET);
    }

    @AfterEach
    public void tearDown() {
        System.out.println(GREEN + "  [✓] Flujo de minería verificado correctamente." + RESET);
    }

    private void step(String msg) { System.out.println(CYAN + "  [PASO] " + RESET + msg); }
    private void info(String key, Object val) { System.out.println(BOLD + "         → " + key + ": " + RESET + val); }
    private void errorLog(String msg) { System.out.println(RED + "  [CONTROL-ERROR] " + RESET + msg); }

    // =========================================================================
    // 1. TEST DE EXTRACCIÓN Y MAPEO (GET)
    // =========================================================================
    @Test @Order(1) @DisplayName("GET: Extracción de Playlist y Transformación de Tags")
    void testExtractionFlow() throws Exception {
        String playlistId = "x174092";
        
        step("1. Simulando respuestas de Dailymotion API (Playlist, Videos, Users, Subtitles)");
        
        // Mock de Playlist (Canal)
        mockServer.expect(requestTo(containsString("/playlist/" + playlistId + "?fields")))
                .andRespond(withSuccess("{\"id\": \"x174092\", \"name\": \"Playlist Test\", \"description\": \"Desc\", \"created_time\": 1705311000}", MediaType.APPLICATION_JSON));

        // Mock de Vídeo con Tags
        String videosJson = "{\"list\": [{\"id\": \"v1\", \"title\": \"Java Video\", \"owner\": \"user1\", \"tags\": [\"programming\", \"java\"]}]}";
        mockServer.expect(requestTo(containsString("/playlist/" + playlistId + "/videos")))
                .andRespond(withSuccess(videosJson, MediaType.APPLICATION_JSON));

        // Mocks obligatorios para cada vídeo encontrado
        mockServer.expect(requestTo(containsString("/user/user1")))
                .andRespond(withSuccess("{\"id\": \"user1\", \"username\": \"Javi\", \"url\": \"http://link\"}", MediaType.APPLICATION_JSON));
        
        mockServer.expect(requestTo(containsString("/video/v1/subtitles")))
                .andRespond(withSuccess("{\"subtitles\": []}", MediaType.APPLICATION_JSON));

        step("2. Ejecutando GET /api/channels/" + playlistId);
        MvcResult result = mockMvc.perform(get("/api/channels/" + playlistId))
                .andExpect(status().isOk())
                .andReturn();

        // VALIDACIÓN MANUAL (Sin jsonPath)
        Channel channel = objectMapper.readValue(result.getResponse().getContentAsString(), Channel.class);

        step("3. Verificando transformación de Tags a Comments");
        assertEquals("x174092", channel.getId());
        assertEquals(1, channel.getVideos().size());
        // En Dailymotion, los tags "programming" y "java" deben ser ahora 2 comentarios
        assertEquals(2, channel.getVideos().get(0).getComments().size(), "Los tags deben haberse convertido en comentarios");
        assertEquals("programming", channel.getVideos().get(0).getComments().get(0).getText());

        mockServer.verify();
        info("Canal", channel.getName());
        info("Comentarios (Tags)", channel.getVideos().get(0).getComments().size());
    }

    // =========================================================================
    // 2. TEST DE INTEGRACIÓN CON VIDEOMINER (POST)
    // =========================================================================
    @Test @Order(2) @DisplayName("POST: Minar y Persistir en VideoMiner (Puerto 8080)")
    void testIntegrationWithVideoMiner() throws Exception {
        String playlistId = "mining-1";

        step("1. Configurando flujo Dailymotion -> Miner -> VideoMiner API");
        
        // Mock Entrada Dailymotion
        mockServer.expect(requestTo(containsString("/playlist/mining-1"))).andRespond(withSuccess("{\"id\": \"m1\", \"name\": \"Miner\"}", MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(containsString("/videos"))).andRespond(withSuccess("{\"list\": []}", MediaType.APPLICATION_JSON));

        // Mock Salida VideoMiner (POST al endpoint central)
        mockServer.expect(requestTo(containsString("/videominer/channels")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(org.springframework.http.HttpStatus.CREATED));

        step("2. Iniciando proceso de minería");
        mockMvc.perform(post("/api/channels/" + playlistId))
                .andExpect(status().isCreated());

        mockServer.verify();
        System.out.println(GREEN + "  [✓] Los datos de Dailymotion fueron enviados con éxito a VideoMiner" + RESET);
    }

    // =========================================================================
    // 3. TEST DE FILTRADO Y PAGINACIÓN IN-MEMORY
    // =========================================================================
    @Test @Order(3) @DisplayName("Lógica: Filtrado por nombre y Paginación segura")
    void testFilteringLogic() throws Exception {
        step("1. Simulando Playlist con 2 vídeos para filtrar");
        mockServer.expect(requestTo(containsString("/playlist/test"))).andRespond(withSuccess("{\"id\": \"t1\", \"name\": \"Test\"}", MediaType.APPLICATION_JSON));
        
        // Enviamos 2 vídeos
        String listJson = "{\"list\": [" +
                "{\"id\": \"v1\", \"title\": \"Tutorial A\", \"owner\": \"u1\"}," +
                "{\"id\": \"v2\", \"title\": \"Tutorial B\", \"owner\": \"u1\"}" +
                "]}";
        mockServer.expect(requestTo(containsString("/videos"))).andRespond(withSuccess(listJson, MediaType.APPLICATION_JSON));

        // Mocks de dependencias para los 2 vídeos
        mockServer.expect(ExpectedCount.manyTimes(), requestTo(containsString("/user/")))
                .andRespond(withSuccess("{\"id\": \"u1\", \"username\": \"Owner\"}", MediaType.APPLICATION_JSON));
        mockServer.expect(ExpectedCount.manyTimes(), requestTo(containsString("/video/")))
                .andRespond(withSuccess("{\"subtitles\": []}", MediaType.APPLICATION_JSON));

        step("2. Realizando búsqueda con ?name=tutorial&size=1");
        MvcResult result = mockMvc.perform(get("/api/channels/test")
                        .param("name", "tutorial")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andReturn();

        Channel channel = objectMapper.readValue(result.getResponse().getContentAsString(), Channel.class);

        step("3. Verificando que la paginación in-memory limitó a 1 resultado");
        assertEquals(1, channel.getVideos().size());
        
        mockServer.verify();
        info("Vídeos tras filtro y página", channel.getVideos().size());
    }

    // =========================================================================
    // 4. TEST DE MANEJO DE ERRORES (404)
    // =========================================================================
    @Test @Order(4) @DisplayName("ERROR: Manejo de Playlist inexistente")
    void testPlaylistNotFound() throws Exception {
        errorLog("Configurando 404 simulado desde Dailymotion API");
        
        mockServer.expect(requestTo(containsString("/playlist/missing")))
                .andRespond(withResourceNotFound());

        mockMvc.perform(get("/api/channels/missing"))
                .andExpect(status().isNotFound());

        System.out.println(GREEN + "  [✓] El Miner gestionó el 404 externo correctamente" + RESET);
    }
}