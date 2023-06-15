import kanban.server.HttpTaskServer;
import kanban.server.KVServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        var t = new KVServer();
        t.start();
        HttpTaskServer server = new HttpTaskServer();
        //TaskManager manager = Managers.getHttpTaskManager();
        //manager.
    }
}
