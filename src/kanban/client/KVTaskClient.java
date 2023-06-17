package kanban.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    protected URI url;
    protected String apiKey;
    protected HttpClient client = HttpClient.newHttpClient();

    public KVTaskClient(String url) {
        this.url = URI.create(url);
        register();
    }

    public void register() {
        URI uri = URI.create(url + "/register");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                apiKey = response.body();
            } else System.out.println("Не удалось получить api ключ");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void put(String key, String json) {
        if (apiKey == null) {
            System.out.println("api ключ отсутствует");
            return;
        }
        URI url = URI.create(this.url + "/save/" + key + "?API_TOKEN=" + apiKey);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String load(String key) {
        if (apiKey == null) {
            return "api ключ отсутствует";
        }

        try {
            URI url = URI.create(this.url + "/load/" + key + "?API_TOKEN=" + apiKey);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }
}
