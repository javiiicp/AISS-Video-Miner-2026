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
@DisplayName("🚀 PeerTube Miner: Certificación de Extracción e Integración")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PeertubeMinerFullIntegrityTests {

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String GREEN = "\u001B[32m";
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
        mockServer = MockRestServiceServer.createServer(restTemplate);
        System.out.println("\n" + PURPLE + "█".repeat(80) + RESET);
        System.out.println(BOLD + YELLOW + " 🛰️ PROBANDO: " + RESET + BOLD + testInfo.getDisplayName().toUpperCase());
        System.out.println(PURPLE + "█".repeat(80) + RESET);
    }

    private void step(String msg) { System.out.println(CYAN + "  [PASO] " + RESET + msg); }
    private void info(String key, Object val) { System.out.println(BOLD + "         → " + key + ": " + RESET + val); }

    // =========================================================================
    // TEST DE FILTRADO (El que fallaba)
    // =========================================================================
    @Test @Order(1) @DisplayName("FILTRO: Validar filtrado de vídeos por nombre")
    void testFilterLogic() throws Exception {
        step("1. Configurando Mock para canal y lista de 2 vídeos");
        
        // 1. Mock Canal
        mockServer.expect(requestTo(containsString("/video-channels/test")))
                .andRespond(withSuccess("{\"id\": 9, \"name\": \"test\"}", MediaType.APPLICATION_JSON));
        
        // 2. Mock Vídeos (Devolvemos uno de Java y otro de Python)
        String videosJson = "{\"total\": 2, \"data\": [" +
                "{\"id\": 1, \"name\": \"Java Tutorial\", \"account\": {\"id\": 5, \"displayName\": \"stux\"}}," +
                "{\"id\": 2, \"name\": \"Python Basics\", \"account\": {\"id\": 5, \"displayName\": \"stux\"}}" +
                "]}";
        mockServer.expect(requestTo(containsString("/videos")))
                .andRespond(withSuccess(videosJson, MediaType.APPLICATION_JSON));

        // IMPORTANTE: Como el servicio recorre todos los vídeos para buscar comentarios/captions,
        // debemos configurar el Mock para que responda a esas llamadas (usamos manyTimes o repetimos)
        
        // 3. Mock Comentarios para los vídeos (esperamos 2 llamadas, una por cada vídeo del JSON de arriba)
        mockServer.expect(ExpectedCount.times(2), requestTo(containsString("/comment-threads")))
                .andRespond(withSuccess("{\"total\": 0, \"data\": []}", MediaType.APPLICATION_JSON));
        
        // 4. Mock Captions para los vídeos (esperamos 2 llamadas)
        mockServer.expect(ExpectedCount.times(2), requestTo(containsString("/captions")))
                .andRespond(withSuccess("{\"total\": 0, \"data\": []}", MediaType.APPLICATION_JSON));

        step("2. Realizando búsqueda '?name=java'");
        MvcResult result = mockMvc.perform(get("/api/channels/test").param("name", "java"))
                .andExpect(status().isOk())
                .andReturn();

        Channel channel = objectMapper.readValue(result.getResponse().getContentAsString(), Channel.class);

        step("3. Verificando filtrado in-memory");
        assertEquals(1, channel.getVideos().size(), "El filtro debería haber dejado solo 1 vídeo");
        assertTrue(channel.getVideos().get(0).getName().contains("Java"));
        
        mockServer.verify();
        info("Vídeos encontrados", channel.getVideos().size());
        System.out.println(GREEN + "  [✓] Lógica de filtrado validada correctamente" + RESET);
    }

    // =========================================================================
    // OTROS TESTS DE INTEGRACIÓN
    // =========================================================================
    @SuppressWarnings("null")
@Test @Order(2) @DisplayName("POST: Minar y Persistir en VideoMiner")
    void testPersistenceFlow() throws Exception {
        String channelId = "javier";
        step("Configurando flujo Miner -> VideoMiner");
        
        mockServer.expect(requestTo(containsString("/video-channels/" + channelId))).andRespond(withSuccess("{\"id\": 1, \"name\": \"javier\"}", MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(containsString("/videos"))).andRespond(withSuccess("{\"total\": 0, \"data\": []}", MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(containsString("/videominer/channels"))).andExpect(method(HttpMethod.POST)).andRespond(withStatus(org.springframework.http.HttpStatus.CREATED));

        mockMvc.perform(post("/api/channels/" + channelId)).andExpect(status().isCreated());
        mockServer.verify();
        System.out.println(GREEN + "  [✓] Integración con VideoMiner validada" + RESET);
    }
}