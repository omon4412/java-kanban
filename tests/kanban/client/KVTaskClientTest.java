package kanban.client;

import kanban.client.KVTaskClient;
import kanban.server.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class KVTaskClientTest {

    static KVTaskClient client;
    static KVServer server;

    @BeforeEach
    public void prepare() throws IOException {
        server = new KVServer();
        server.start();
        client = new KVTaskClient("http://localhost:8078");
    }
    @AfterEach
    public void stop() throws IOException {
        server.stop();
    }

    @Test
    void register() {
        client.register();
        assertNotNull(client.apiKey);
    }

    @Test
    void put() {
        register();
        client.put("key", "value");
        assertEquals(client.load("key"), "value");
    }

    @Test
    void update() {
        register();
        put();
        client.put("key", "value78");
        assertEquals(client.load("key"), "value78");
    }
}