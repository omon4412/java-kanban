package kanban.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kanban.util.InstantAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class KVServerTest {

    static KVServer server;
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    static HttpClient client;
    static String apiKey;

    @BeforeEach
    public void setupData() throws IOException, InterruptedException {
        server = new KVServer();
        server.start();
        gson = new Gson();
        client = HttpClient.newHttpClient();
        register();
    }

    @AfterEach
    public void stopServer() throws IOException {
        server.stop();
    }

    public void register() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/register");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        apiKey = response.body();
    }

    @Test
    public void shouldNotAuthorizedByNoApiKey() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8078/save");
        String json = gson.toJson("test");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    public void shouldNotAuthorizedByWorthApiKey() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8078/save/test?API_TOKEN=0");
        String json = gson.toJson("test");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    public void shouldRegister() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/register");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldNotRegister() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/register");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }

    @Test
    public void shouldSaveData() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/save/test?API_TOKEN=" + apiKey);
        String json = gson.toJson("test");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Не авторизирован");
    }

    @Test
    public void shouldNotSaveDataWithNoKey() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/save/?API_TOKEN=" + apiKey);
        String json = gson.toJson("test");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }
    @Test
    public void shouldNotSaveDataWithNoValue() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/save/key?API_TOKEN=" + apiKey);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void shouldNotSave() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/save/key?API_TOKEN=" + apiKey);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("value");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }

    @Test
    public void shouldLoadData() throws IOException, InterruptedException {
        shouldSaveData();
        URI url = URI.create("http://localhost:8078/load/test?API_TOKEN=" + apiKey);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Не авторизирован");
    }

    @Test
    public void shouldNotLoadDataWithEmptyKey() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/load/?API_TOKEN=" + apiKey);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

}