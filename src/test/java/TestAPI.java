import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.http.HttpResponse;

public class TestAPI {
    static String apiHost;
    static String oauth;

    static String testCopyDirectoryName = "testCopy";
    static String testCreateDirectoryName = "test";
    static String testDeleteDirectoryName = "testDelete";

    static boolean testCopyNeedCleanup = false;
    static boolean testCreateNeedCleanup = false;

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

        setupTests();
    }

    static void setupTests(){
        createSampleDirectory(testCopyDirectoryName);
        testCopyNeedCleanup = true;

        createSampleDirectory(testDeleteDirectoryName);
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

    @Test
    void createDirectory(){
        resource = "/resources";
        method = "PUT";
        parameters = new String[]{"path="+testCreateDirectoryName};

        APIHandler handler = new APIHandler(resource, method, parameters);

        HttpResponse<String> rsp;
        try {
            rsp = handler.getResponse();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(201, rsp.statusCode());

        testCreateNeedCleanup = true;
    }

    @Test
    void copyDirectory(){
        resource = "/resources/copy";
        method = "POST";
        parameters = new String[]{"from="+testCopyDirectoryName,"path="+testCopyDirectoryName+"2"};

        APIHandler handler = new APIHandler(resource, method, parameters);

        HttpResponse<String> rsp;
        try {
            rsp = handler.getResponse();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(rsp.body());
        Assertions.assertEquals(201, rsp.statusCode());
    }

    @Test
    void deleteDirectory(){
        resource = "/resources";
        method = "DELETE";
        parameters = new String[]{"path="+testDeleteDirectoryName};

        APIHandler handler = new APIHandler(resource, method, parameters);

        HttpResponse<String> rsp;
        try {
            rsp = handler.getResponse();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(204, rsp.statusCode());

    }

    static void createSampleDirectory(String name){
        APIHandler handler = new APIHandler("/resources", "PUT", new String[]{"path="+name});

        HttpResponse<String> rsp;
        try {
            rsp = handler.getResponse();
            if (rsp.statusCode() != 201){
                throw new IOException();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static void deleteSampleDirectory(String name){
        APIHandler handler = new APIHandler("/resources", "DELETE", new String[]{"path="+name});

        HttpResponse<String> rsp;
        try {
            rsp = handler.getResponse();
            if (rsp.statusCode() != 204){
                throw new IOException();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @AfterAll
    static void cleanUpAndWait(){
        if (testCopyNeedCleanup){
            testCopyNeedCleanup = false;
            deleteSampleDirectory(testCopyDirectoryName);
            deleteSampleDirectory(testCopyDirectoryName+"2");
        }
        if (testCreateNeedCleanup){
            testCreateNeedCleanup = false;
            deleteSampleDirectory(testCreateDirectoryName);
        }
    }
}
