package nets150.group.maven.eclipse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import kong.unirest.HttpResponse;

import org.junit.Test;

import kong.unirest.JsonNode;
import kong.unirest.Unirest;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void shouldReturnOkay() {
        HttpResponse<JsonNode> jsonResponse = Unirest.get("https://finnhub.io/api/v1")
                .header("X-Finnhub-Token", "c20ra02ad3iec96dij7g")
                .asJson();
        
        assertEquals(200, jsonResponse.getStatus()); 
    }
}
