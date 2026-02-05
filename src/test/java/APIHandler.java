import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APIHandler {
    static String oauth;
    static String host;
    HttpClient client;
    HttpRequest request;

    static void setAuth(String token){
        oauth = token;
    }

    static void setHost(String url){
        host = url;
    }

    APIHandler(String resource, String method, String[] parameters){
        client = HttpClient.newHttpClient();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        String URIString =  host + resource + "?" + String.join("&", parameters);
        requestBuilder.uri(URI.create(URIString));

        switch (method) {
            case "GET":
                requestBuilder.GET();
                break;
            case "POST":
                requestBuilder.POST(HttpRequest.BodyPublishers.noBody());
                break;
            case "PUT":
                requestBuilder.PUT(HttpRequest.BodyPublishers.noBody());
                break;
            case "DELETE":
                requestBuilder.DELETE();
                break;
        }

        requestBuilder.header("Authorization", "OAuth " + oauth);

        request = requestBuilder.build();
    }

    public HttpResponse<String> getResponse() throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
