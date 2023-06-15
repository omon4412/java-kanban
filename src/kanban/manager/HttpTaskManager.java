package kanban.manager;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import kanban.models.Epic;
import kanban.models.Subtask;
import kanban.models.Task;
import kanban.client.KVTaskClient;
import kanban.util.InstantAdapter;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

public class HttpTaskManager extends FileBackedTasksManager {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();
    private final KVTaskClient client;

    public HttpTaskManager(String url) {
        client = new KVTaskClient(url);
    }

    @Override
    protected void save() {
        var jsonAllTasks = gson.toJson(prioritizedTasks);
        client.put("allTasks", jsonAllTasks);

        var jsonTasks = gson.toJson(tasks);
        client.put("tasks", jsonTasks);

        var jsonEpics = gson.toJson(epics);
        client.put("epics", jsonEpics);

        var jsonSubtasks = gson.toJson(subtasks);
        client.put("subtasks", jsonSubtasks);

        var jsonHistory = gson.toJson(getHistory());
        client.put("history", jsonHistory);
    }

    public void load() {
        var jsonAllTasks = client.load("allTasks");
        prioritizedTasks.addAll(gson.fromJson(jsonAllTasks, new TypeToken<Set<Task>>() {
        }.getType()));

        var jsonTasks = client.load("tasks");
        tasks.putAll(gson.fromJson(jsonTasks, new TypeToken<Map<Integer, Task>>() {
        }.getType()));

        var jsonEpics = client.load("epics");
        epics.putAll(gson.fromJson(jsonEpics, new TypeToken<Map<Integer, Epic>>() {
        }.getType()));

        var jsonSubtasks = client.load("subtasks");
        subtasks.putAll(gson.fromJson(jsonSubtasks, new TypeToken<Map<Integer, Subtask>>() {
        }.getType()));

        var jsonHistory = client.load("history");
        JsonArray jsonHistoryArray = JsonParser.parseString(jsonHistory).getAsJsonArray();
        for (JsonElement jsonElement : jsonHistoryArray) {
            inMemoryHistoryManager.add(gson.fromJson(jsonElement, Task.class));
        }
    }
}
