package aiss.peertube_miner;

import static org.hamcrest.Matchers.containsString;

import aiss.peertube_miner.model.Channel;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
@DisplayName("🚀 PeerTube Miner: Certificación de Extracción e Integración")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PeertubeMinerFullIntegrityTests {

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

    @SuppressWarnings("null")
@BeforeEach
    public void setUp(TestInfo testInfo) {
        // Inicializamos el servidor de mocks para simular las APIs externas (PeerTube) e internas (VideoMiner)
        mockServer = MockRestServiceServer.createServer(restTemplate);
        
        System.out.println("\n" + PURPLE + "█".repeat(80) + RESET);
        System.out.println(BOLD + YELLOW + " 🛰️ PROBANDO: " + RESET + BOLD + testInfo.getDisplayName().toUpperCase());
        System.out.println(PURPLE + "█".repeat(80) + RESET);
    }

    @AfterEach
    public void tearDown() {
        System.out.println(GREEN + "  [✓] Fase de prueba finalizada correctamente." + RESET);
    }

    private void step(String msg) { System.out.println(CYAN + "  [PASO] " + RESET + msg); }
    private void info(String key, Object val) { System.out.println(BOLD + "         → " + key + ": " + RESET + val); }
    private void errorLog(String msg) { System.out.println(RED + "  [CONTROL-ERROR] " + RESET + msg); }

    // =========================================================================
    // 1. TEST DE EXTRACCIÓN Y MAPEO (GET)
    // =========================================================================
    @SuppressWarnings("null")
@Test @Order(1) @DisplayName("GET: Extracción de PeerTube y Mapeo a Modelo Interno")
    void testExtractionFlow() throws Exception {
        String channelId = "stux";
        
        step("1. Simulando respuestas de la API de PeerTube (Canal, Vídeos, Comentarios, Captions)");
        
        // Mock de Canal
        mockServer.expect(requestTo(containsString("/video-channels/" + channelId)))
                .andRespond(withSuccess("{\"id\": 80, \"name\": \"stux\", \"displayName\": \"Stux TV\", \"createdAt\": \"2024-01-01\"}", MediaType.APPLICATION_JSON));

        // Mock de Vídeos con Autor (account) incluido para evitar NPE en el Mapper
        mockServer.expect(requestTo(containsString("/videos")))
                .andRespond(withSuccess("{\"total\": 1, \"data\": [{\"id\": 10, \"name\": \"Video Test\", \"publishedAt\": \"2026-01-01\", \"account\": {\"id\": 5, \"displayName\": \"stux_user\", \"url\": \"http://link\"}}]}", MediaType.APPLICATION_JSON));

        // Mocks obligatorios para hilos de comentarios y subtítulos
        mockServer.expect(requestTo(containsString("/comment-threads"))).andRespond(withSuccess("{\"total\": 0, \"data\": []}", MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(containsString("/captions"))).andRespond(withSuccess("{\"total\": 0, \"data\": []}", MediaType.APPLICATION_JSON));

        step("2. Ejecutando petición al controlador GET /api/channels/" + channelId);
        MvcResult result = mockMvc.perform(get("/api/channels/" + channelId))
                .andExpect(status().isOk())
                .andReturn();

        // VALIDACIÓN MANUAL (Sin jsonPath para evitar errores de librería)
        Channel channel = objectMapper.readValue(result.getResponse().getContentAsString(), Channel.class);

        step("3. Validando integridad de los datos transformados");
        assertEquals("80", channel.getId());
        assertEquals("stux", channel.getName());
        assertFalse(channel.getVideos().isEmpty(), "Debería contener 1 vídeo");
        assertEquals("stux_user", channel.getVideos().get(0).getAuthor().getName());

        mockServer.verify();
        info("Canal Mapeado", channel.getName());
        info("Vídeos detectados", channel.getVideos().size());
    }

    // =========================================================================
    // 2. TEST DE INTEGRACIÓN CON VIDEOMINER (POST)
    // =========================================================================
    @SuppressWarnings("null")
@Test @Order(2) @DisplayName("POST: Minar y Persistir en VideoMiner (Puerto 8080)")
    void testPersistenceFlow() throws Exception {
        String channelId = "javier";

        step("1. Configurando flujo completo PeerTube -> Miner -> VideoMiner API");
        
        // Mock entrada de PeerTube
        mockServer.expect(requestTo(containsString("/video-channels/" + channelId))).andRespond(withSuccess("{\"id\": 1, \"name\": \"javier\"}", MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(containsString("/videos"))).andRespond(withSuccess("{\"total\": 0, \"data\": []}", MediaType.APPLICATION_JSON));

        // Mock salida hacia VideoMiner (Validando que el Miner haga el POST correctamente)
        mockServer.expect(requestTo(containsString("/videominer/channels")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(org.springframework.http.HttpStatus.CREATED));

        step("2. Iniciando proceso de minería mediante POST /api/channels/" + channelId);
        mockMvc.perform(post("/api/channels/" + channelId))
                .andExpect(status().isCreated());

        mockServer.verify();
        System.out.println(GREEN + "  [✓] Canal enviado y persistido correctamente en la API central" + RESET);
    }

    // =========================================================================
    // 3. TEST DE FILTRADO Y PAGINACIÓN IN-MEMORY
    // =========================================================================
    @SuppressWarnings("null")
@Test @Order(3) @DisplayName("FILTRO: Validar lógica de filtrado de vídeos")
    void testFilterLogic() throws Exception {
        step("1. Simulando PeerTube con 2 vídeos (Java y Python)");
        mockServer.expect(requestTo(containsString("/video-channels/test"))).andRespond(withSuccess("{\"id\": 9, \"name\": \"test\"}", MediaType.APPLICATION_JSON));
        
        // CORRECCIÓN CRÍTICA: Se añade el objeto "account" a cada vídeo para que PeertubeMapper.toUser() no lance NPE
        String videosJson = "{\"total\": 2, \"data\": [" +
                "{\"id\": 1, \"name\": \"Java Tutorial\", \"account\": {\"id\": 5, \"displayName\": \"stux\"}}," +
                "{\"id\": 2, \"name\": \"Python Basics\", \"account\": {\"id\": 5, \"displayName\": \"stux\"}}" +
                "]}";
        
        mockServer.expect(requestTo(containsString("/videos"))).andRespond(withSuccess(videosJson, MediaType.APPLICATION_JSON));

        step("2. Realizando búsqueda con parámetro '?name=java'");
        MvcResult result = mockMvc.perform(get("/api/channels/test").param("name", "java"))
                .andExpect(status().isOk())
                .andReturn();

        Channel channel = objectMapper.readValue(result.getResponse().getContentAsString(), Channel.class);

        step("3. Verificando que el filtro in-memory del Servicio funciona");
        assertEquals(1, channel.getVideos().size(), "El filtro debería haber dejado solo 1 vídeo");
        assertTrue(channel.getVideos().get(0).getName().contains("Java"));
        
        info("Resultados tras filtrado", channel.getVideos().size());
    }

    // =========================================================================
    // 4. TEST DE ERRORES (404)
    // =========================================================================
    @SuppressWarnings("null")
@Test @Order(4) @DisplayName("ERROR: Gestión de 404 desde API externa")
    void testChannelNotFound() throws Exception {
        errorLog("Configurando 404 simulado desde PeerTube para canal 'inexistente'");
        
        mockServer.expect(requestTo(containsString("/video-channels/404")))
                .andRespond(withResourceNotFound());

        mockMvc.perform(get("/api/channels/404"))
                .andExpect(status().isNotFound());

        System.out.println(GREEN + "  [✓] El Miner gestionó correctamente el error de PeerTube" + RESET);
    }
}