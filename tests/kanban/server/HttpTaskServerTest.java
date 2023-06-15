package kanban.server;

import kanban.manager.HttpTaskManager;
import kanban.models.Epic;
import kanban.models.Subtask;
import kanban.models.Task;
import kanban.models.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

import static kanban.server.KVServerTest.gson;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    static KVServer kvServer;
    HttpTaskServer httpTaskServer;
    HttpClient client;

    public Task task;
    public Epic epic;
    public Subtask subtask;
    public Task task2;
    public Epic epic2;
    public Subtask subtask2;

    @BeforeEach
    public void init() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        client = HttpClient.newHttpClient();
        task = new Task("Task name", "Task desc", TaskStatus.NEW);
        epic = new Epic("Epic name", "Epic desc");
        subtask = new Subtask("Sub name", "Sub desc", TaskStatus.NEW);
        task2 = new Task("Task name2", "Task desc2", TaskStatus.NEW);
        epic2 = new Epic("Epic name2", "Epic desc2");
        subtask2 = new Subtask("Sub name2", "Sub desc2", TaskStatus.NEW);
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
        httpTaskServer.stop();
        HttpTaskServer.getTaskManager().clearTasks();
        //HttpTaskServer.getTaskManager().clearSubtasks();
        HttpTaskServer.getTaskManager().clearEpics();
        HttpTaskServer.getTaskManager().setLastId(0);
    }

    public void addTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(task2);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/epic");
        json = gson.toJson(epic);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(epic2);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask");
        subtask.setEpicId(3);
        json = gson.toJson(subtask);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask");
        subtask2.setEpicId(4);
        json = gson.toJson(subtask2);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void shouldGetEmptyListOfAllTasks() throws IOException, InterruptedException {
        var t = HttpTaskServer.taskManager;
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    public void shouldGetListOfAllTasks() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        var expectedValue = gson.toJson(HttpTaskServer.getTaskManager().getPrioritizedTasks());
        assertEquals(expectedValue, response.body());
    }

    @Test
    public void shouldGetEmptyListOfTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    public void shouldGetListOfTasks() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(HttpTaskServer.getTaskManager().getTasks()), response.body());
    }

    @Test
    public void shouldAddTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача с id=1 добавлена", response.body());
    }

    @Test
    public void shouldNotAddTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        task.setName("");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Поля не могут быть пустыми", response.body());
    }

    @Test
    public void shouldUpdateTask() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/task");
        task.setStatus(TaskStatus.DONE);
        task.setId(1);
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача с id=1 обновлена", response.body());
    }

    @Test
    public void shouldNotUpdateTask() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/task");
        task.setId(888);
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Ошибка обновления задачи", response.body());
    }

    @Test
    public void shouldDeleteTask() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача с идендификатором 1 успешно удалена", response.body());
    }

    @Test
    public void shouldDeleteAllTasks() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Все задачи удалены", response.body());
    }

    @Test
    public void shouldGetTaskById() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(HttpTaskServer.getTaskManager().getTaskById(1)), response.body());
    }

    @Test
    public void shouldNotGetTaskById() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/task?id=9999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача с таким идендификатором не найдена", response.body());
    }

    @Test
    public void shouldGetEmptyListOfEpics() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    public void shouldGetListOfEpics() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(HttpTaskServer.getTaskManager().getEpics()), response.body());
    }

    @Test
    public void shouldAddEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача с id=1 добавлена", response.body());
    }

    @Test
    public void shouldNotAddEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic");
        epic.setStatus(TaskStatus.DONE);
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Поле статус не задаётся вручную", response.body());
    }

    @Test
    public void shouldUpdateEpic() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        epic.setDescription("updates");
        epic.setId(3);
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача с id=3 обновлена", response.body());
    }

    @Test
    public void shouldDeleteEpic() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/epic?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача с идендификатором 3 успешно удалена", response.body());
    }

    @Test
    public void shouldDeleteAllEpics() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Все задачи удалены", response.body());
    }

    @Test
    public void shouldNotDeleteEpic() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/epic?id=456465");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Такого идентификатора нет", response.body());
    }

    @Test
    public void shouldGetEpicById() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/epic?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(HttpTaskServer.getTaskManager().getEpicById(3)), response.body());
    }

    @Test
    public void shouldNotGetEpicById() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/epic?id=7978");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача с таким идендификатором не найдена", response.body());
    }

    @Test
    public void shouldGetEmptyListOfSubtasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    public void shouldGetListOfSubtasks() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(HttpTaskServer.getTaskManager().getSubtasks()), response.body());
    }

    @Test
    public void shouldAddSubtask() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        subtask.setEpicId(3);
        String json = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача с id=7 добавлена", response.body());
    }

    @Test
    public void shouldNotAddSubtask() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        subtask.setEpicId(456456);
        String json = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Эпика для подзадачи не существует", response.body());
    }

    @Test
    public void shouldUpdateSubtask() throws IOException, InterruptedException {
        addTasks();
        var t = HttpTaskServer.taskManager;
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        subtask.setStatus(TaskStatus.DONE);
        subtask.setId(5);
        subtask.setEpicId(3);
        String json = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача с id=5 обновлена", response.body());
    }

    @Test
    public void shouldDeleteSubtask() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/subtask?id=5");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача с идендификатором 5 успешно удалена", response.body());
    }

    @Test
    public void shouldDeleteAllSubtasks() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Все задачи удалены", response.body());
    }

    @Test
    public void shouldNotDeleteSubtask() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/subtask?id=45654");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Такого идентификатора нет", response.body());
    }

    @Test
    public void shouldNotDeleteSubtaskByIncorrectId() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/subtask?id=string");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Неверный идендификатор задачи", response.body());
    }

    @Test
    public void shouldGetSubtaskById() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/subtask?id=5");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(HttpTaskServer.getTaskManager().getSubtaskById(5)), response.body());
    }

    @Test
    public void shouldNotGetSubtaskById() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/subtask?id=5546546");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача с таким идендификатором не найдена", response.body());
    }

    @Test
    public void shouldWriteNoValidId() throws IOException, InterruptedException {
        addTasks();
        URI url = URI.create("http://localhost:8080/tasks/subtask?id=string");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Неверный идендификатор задачи", response.body());
    }

    @Test
    public void shouldWriteNoValidJSON() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic");
        String json = gson.toJson("sfdkhjlkdsflkjsdf");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Получен некорректный JSON", response.body());
    }

    @Test
    public void shouldGetEmptyHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/history");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    public void shouldGetHistory() throws IOException, InterruptedException {
        addTasks();
        HttpTaskServer.getTaskManager().getTaskById(2);
        HttpTaskServer.getTaskManager().getTaskById(1);
        URI url = URI.create("http://localhost:8080/tasks/history");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(HttpTaskServer.getTaskManager().getHistory()), response.body());
    }

    @Test
    public void shouldNotAdded() throws IOException, InterruptedException {
        Instant now = Instant.now();
        Task t1 = new Task("1", "sd", now, 10);
        Task t2 = new Task("1", "rt", now, 10);
        URI url = URI.create("http://localhost:8080/tasks/task");
        String json = gson.toJson(t1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        json = gson.toJson(t2);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Обнаружено перечечение задач", response.body());
    }
}