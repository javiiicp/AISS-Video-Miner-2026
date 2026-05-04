package aiss.dailymotion_miner.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Caption model tests")
class CaptionValidationTests {

    @Test
    @DisplayName("Debe guardar y devolver id/link/language")
    void shouldStoreBasicFields() {
        Caption caption = new Caption();

        caption.setId("cap_1");
        caption.setLink("https://example.com/sub.vtt");
        caption.setLanguage("es");

        assertEquals("cap_1", caption.getId());
        assertEquals("https://example.com/sub.vtt", caption.getLink());
        assertEquals("es", caption.getLanguage());
    }

    @Test
    @DisplayName("toString debe incluir los campos principales")
    void shouldRenderToStringWithFields() {
        Caption caption = new Caption();
        caption.setId("cap_2");
        caption.setLink("https://example.com/en.vtt");
        caption.setLanguage("en");

        String result = caption.toString();

        assertNotNull(result);
        assertTrue(result.contains("cap_2"));
        assertTrue(result.contains("https://example.com/en.vtt"));
        assertTrue(result.contains("en"));
    }
}
