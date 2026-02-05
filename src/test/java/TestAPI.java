import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.http.HttpResponse;

public class TestAPI {
    static String apiHost;
    static String oauth;
    String resource;
    String method;
    String[] parameters;

    @BeforeAll
    static void setupEnv() {
        Dotenv dotenv = Dotenv.load();
        apiHost = dotenv.get("API_HOST");
        oauth = dotenv.get("OAUTH_TOKEN");
        APIHandler.setHost(apiHost);
        APIHandler.setAuth(oauth);
    }

    @Test
    void testConnection(){
        resource = "";
        method = "GET";
        parameters = new String[]{};

        APIHandler handler = new APIHandler(resource, method, parameters);

        HttpResponse<String> rsp;
        try {
            rsp = handler.getResponse();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(200, rsp.statusCode());
    }
}
