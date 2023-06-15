package kanban.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import kanban.manager.IntersectionDetectedException;
import kanban.manager.Managers;
import kanban.manager.TaskManager;
import kanban.models.*;
import kanban.util.InstantAdapter;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static kanban.models.ContentType.JSON;
import static kanban.models.ContentType.TEXT;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer httpServer;
    static TaskManager taskManager = Managers.getHttpTaskManager();

    public static TaskManager getTaskManager() {
        return taskManager;
    }

    public void stop() {
        httpServer.stop(1);
    }

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    public HttpTaskServer() {
        try {
            httpServer = HttpServer.create();

            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TasksHandler());

            httpServer.start();

            System.out.println("HTTP-сервер запущен на " + PORT + " порту!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if ((path.equals("/tasks/") || (path.equals("/tasks"))) && method.equals("GET")) {
                List<Task> prioritizedTasks = new ArrayList<>(taskManager.getPrioritizedTasks());
                writeResponse(exchange, gson.toJson(prioritizedTasks), HTTP_OK, JSON);
            } else if (path.equals("/tasks/task/") || path.equals("/tasks/task")) {
                if (method.equals("GET")) {
                    getTasks(exchange, taskManager, TaskType.TASK);
                    return;
                } else if (method.equals("DELETE")) {
                    deleteTasks(exchange, taskManager, TaskType.TASK);
                    return;
                } else if (method.equals("POST")) {
                    addOrUpdateTasks(exchange, TaskType.TASK, false);
                    return;
                } else if (method.equals("PUT")) {
                    addOrUpdateTasks(exchange, TaskType.TASK, true);
                    return;
                }
            } else if (path.equals("/tasks/epic/") || path.equals("/tasks/epic")) {
                if (method.equals("GET")) {
                    getTasks(exchange, taskManager, TaskType.EPIC);
                    return;
                } else if (method.equals("DELETE")) {
                    deleteTasks(exchange, taskManager, TaskType.EPIC);
                    return;
                } else if (method.equals("POST")) {
                    addOrUpdateTasks(exchange, TaskType.EPIC, false);
                } else if (method.equals("PUT")) {
                    addOrUpdateTasks(exchange, TaskType.EPIC, true);
                }
            } else if (path.equals("/tasks/subtask/") || path.equals("/tasks/subtask")) {
                if (method.equals("GET")) {
                    getTasks(exchange, taskManager, TaskType.SUBTASK);
                    return;
                } else if (method.equals("DELETE")) {
                    deleteTasks(exchange, taskManager, TaskType.SUBTASK);
                    return;
                } else if (method.equals("POST")) {
                    addOrUpdateTasks(exchange, TaskType.SUBTASK, false);
                } else if (method.equals("PUT")) {
                    addOrUpdateTasks(exchange, TaskType.SUBTASK, true);
                }
            } else if ((path.equals("/tasks/history/") || path.equals("/tasks/history")) && method.equals("GET")) {
                List<Task> history = new ArrayList<>(taskManager.getHistory());
                writeResponse(exchange, gson.toJson(history), HTTP_OK, JSON);
            }
            writeResponse(exchange, "Такого эндпоинта не существует", HTTP_NOT_FOUND, TEXT);
        }

        private void addOrUpdateTasks(HttpExchange exchange, TaskType taskType, boolean isUpdate) throws IOException {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes());
                addTasks(exchange, taskManager, taskType, body, isUpdate);
                writeResponse(exchange, body, HTTP_OK, JSON);
            } catch (JsonSyntaxException ex) {
                writeResponse(exchange, "Получен некорректный JSON", HTTP_BAD_REQUEST, TEXT);
            } catch (IntersectionDetectedException ex) {
                writeResponse(exchange, "Обнаружено перечечение задач", HTTP_BAD_REQUEST, TEXT);
            }
        }

        private void getTasks(HttpExchange exchange, TaskManager taskManager, TaskType type) throws IOException {
            String idFromQuery = getTaskIdFromQuery(exchange.getRequestURI());

            try {
                int id = -1;
                if (idFromQuery != null) {
                    id = Integer.parseInt(idFromQuery);
                }
                switch (type) {
                    case TASK:
                        if (id == -1) {
                            writeResponse(exchange, gson.toJson(taskManager.getTasks()), HTTP_OK, JSON);
                            return;
                        }
                        Task task = taskManager.getTaskById(id);
                        if (task != null) {
                            writeResponse(exchange, gson.toJson(task), HTTP_OK, JSON);
                        } else {
                            writeResponse(exchange,
                                    "Задача с таким идендификатором не найдена", HTTP_NOT_FOUND, TEXT);
                        }
                        break;
                    case EPIC:
                        if (id == -1) {
                            writeResponse(exchange, gson.toJson(taskManager.getEpics()), HTTP_OK, JSON);
                            return;
                        }
                        Epic epic = taskManager.getEpicById(id);
                        if (epic != null) {
                            writeResponse(exchange, gson.toJson(epic), HTTP_OK, JSON);
                        } else {
                            writeResponse(exchange,
                                    "Задача с таким идендификатором не найдена", HTTP_NOT_FOUND, TEXT);
                        }
                        break;
                    case SUBTASK:
                        if (id == -1) {
                            writeResponse(exchange, gson.toJson(taskManager.getSubtasks()), HTTP_OK, JSON);
                            return;
                        }
                        Subtask subtask = taskManager.getSubtaskById(id);
                        if (subtask != null) {
                            writeResponse(exchange, gson.toJson(subtask), HTTP_OK, JSON);
                        } else {
                            writeResponse(exchange,
                                    "Задача с таким идендификатором не найдена", HTTP_NOT_FOUND, TEXT);
                        }
                        break;
                    default:
                        writeResponse(exchange,
                                "Такого типа задачи не существует", HTTP_BAD_REQUEST, TEXT);
                }

            } catch (NumberFormatException ex) {
                writeResponse(exchange, "Неверный идендификатор задачи", HTTP_BAD_REQUEST, TEXT);
            }

            writeResponse(exchange, gson.toJson(taskManager.getTasks()), HTTP_OK, JSON);
        }

        private void deleteTasks(HttpExchange exchange, TaskManager taskManager, TaskType type) throws IOException {
            String idFromQuery = getTaskIdFromQuery(exchange.getRequestURI());

            try {
                int id = -1;
                if (idFromQuery != null) {
                    id = Integer.parseInt(idFromQuery);
                }

                Boolean isDeleted = null;
                switch (type) {
                    case TASK:
                        if (id == -1) {
                            taskManager.clearTasks();
                        } else {
                            isDeleted = taskManager.deleteTask(id);
                        }
                        break;
                    case EPIC:
                        if (id == -1) {
                            taskManager.clearEpics();
                        } else {
                            isDeleted = taskManager.deleteEpicById(id);
                        }
                        break;
                    case SUBTASK:
                        if (id == -1) {
                            taskManager.clearSubtasks();
                        } else {
                            isDeleted = taskManager.deleteSubtaskById(id);
                        }
                        break;
                    default:
                        writeResponse(exchange,
                                "Такого типа задачи не существует", HTTP_BAD_REQUEST, TEXT);
                }

                if (Boolean.TRUE.equals(isDeleted)) {
                    writeResponse(exchange,
                            "Задача с идендификатором " + id + " успешно удалена", HTTP_OK, TEXT);
                } else if (Boolean.FALSE.equals(isDeleted)) {
                    writeResponse(exchange,
                            "Такого идентификатора нет", HTTP_NOT_FOUND, TEXT);
                } else if (isDeleted == null) {
                    writeResponse(exchange,
                            "Все задачи удалены", HTTP_OK, TEXT);
                }

            } catch (NumberFormatException ex) {
                writeResponse(exchange, "Неверный идендификатор задачи", HTTP_BAD_REQUEST, TEXT);
            }
        }

        private void addTasks(HttpExchange exchange, TaskManager taskManager,
                              TaskType type, String body, boolean isUpdate) throws IOException {

            try {
                int id = -2;
                switch (type) {
                    case TASK:
                        Task task = gson.fromJson(body, Task.class);
                        if (task.getName().isEmpty() || task.getStatus() == null) {
                            writeResponse(exchange, "Поля не могут быть пустыми", HTTP_BAD_REQUEST, TEXT);
                            return;
                        }
                        if (isUpdate) {
                            if (!taskManager.updateTask(task)) {
                                id = -3;
                            } else {
                                id = task.getId();
                            }
                        } else {
                            id = taskManager.addTask(task);
                        }
                        break;
                    case EPIC:
                        Epic epic = gson.fromJson(body, Epic.class);
                        if (epic.getName().isEmpty()) {
                            writeResponse(exchange, "Поля не могут быть пустыми", HTTP_BAD_REQUEST, TEXT);
                            return;
                        } else if (epic.getStatus() != TaskStatus.NEW) {
                            writeResponse(exchange, "Поле статус не задаётся вручную",
                                    HTTP_BAD_REQUEST, TEXT);
                            return;
                        }
                        if (isUpdate) {
                            if (!taskManager.updateEpic(epic)) {
                                id = -3;
                            } else {
                                id = epic.getId();
                            }
                        } else {
                            id = taskManager.addEpic(epic);
                        }
                        break;
                    case SUBTASK:
                        Subtask sub = gson.fromJson(body, Subtask.class);
                        if (sub.getName().isEmpty() || sub.getStatus() == null || sub.getEpicId() == -1) {
                            writeResponse(exchange, "Поля не могут быть пустыми", HTTP_BAD_REQUEST, TEXT);
                            return;
                        }
                        if (isUpdate) {
                            if (!taskManager.updateSubtask(sub)) {
                                id = -3;
                            } else {
                                id = sub.getId();
                            }
                        } else {
                            id = taskManager.addSubtaskToEpic(sub);
                        }
                        break;
                    default:
                        writeResponse(exchange,
                                "Такого типа задачи не существует", HTTP_BAD_REQUEST, TEXT);
                }

                if (id == -3) {
                    writeResponse(exchange,
                            "Ошибка обновления задачи", HTTP_BAD_REQUEST, TEXT);
                }
                if (id == -2) {
                    writeResponse(exchange,
                            "Ошибка добавления задачи", HTTP_BAD_REQUEST, TEXT);
                } else if (id == -1) {
                    writeResponse(exchange,
                            "Эпика для подзадачи не существует", HTTP_BAD_REQUEST, TEXT);
                } else if (isUpdate && id != -3) {
                    writeResponse(exchange,
                            "Задача с id=" + id + " обновлена", HTTP_OK, TEXT);
                } else {
                    writeResponse(exchange,
                            "Задача с id=" + id + " добавлена", HTTP_OK, TEXT);
                }

            } catch (NumberFormatException ex) {
                writeResponse(exchange, "Неверный идендификатор задачи", HTTP_BAD_REQUEST, TEXT);
            }
        }

        private void writeResponse(HttpExchange exchange,
                                   String responseString,
                                   int responseCode, ContentType type) throws IOException {
            if (responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                exchange.getResponseHeaders().add("X-Application-Name", "Java Kanban");
                exchange.getResponseHeaders().add("Content-Type", type.label);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }

        private String getTaskIdFromQuery(URI uri) {
            String query = uri.getQuery();
            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2 && keyValue[0].equals("id")) {
                        return keyValue[1];
                    }
                }
            }
            return null;
        }
    }
}
