package kanban.manager;

import kanban.models.Epic;
import kanban.models.Subtask;
import kanban.models.Task;
import kanban.models.TaskType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final Path path;

    private FileBackedTasksManager(String path) {
        this.path = Paths.get(path);
        try {
            if (!Files.exists(this.path)) {
                Files.createFile(this.path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Заполнение менеджера данными из файла
     *
     * @param file - Путь к файлу
     * @return Объект менеджера {@link TaskManager}
     */
    public static TaskManager loadFromFile(File file) {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(file.getAbsolutePath());

        try {
            String[] lines = Files.readString(tasksManager.getPath()).split("\n");
            if (lines.length < 1) {
                return tasksManager;
            }

            String historyLine = lines[lines.length - 1];
            var history = historyFromString(historyLine);
            int maxId = 0;

            for (int i = 1; i < lines.length - 2; i++) {
                String[] data = lines[i].split(",");

                switch (TaskType.valueOf(data[1])) {
                    case TASK:
                        Task task = new Task();
                        addTaskToManagerAndHistory(task, lines[i], tasksManager.tasks, tasksManager, history);
                        maxId = Integer.max(maxId, task.getId());
                        break;
                    case EPIC:
                        Epic epic = new Epic();
                        addTaskToManagerAndHistory(epic, lines[i], tasksManager.epics, tasksManager, history);
                        maxId = Integer.max(maxId, epic.getId());
                        break;
                    case SUBTASK:
                        Subtask sub = new Subtask();
                        addTaskToManagerAndHistory(sub, lines[i], tasksManager.subtasks, tasksManager, history);

                        int subId = sub.getId();
                        var curEpic = tasksManager.epics.get(sub.getEpicId());
                        curEpic.getSubtasks().add(subId);

                        maxId = Integer.max(maxId, sub.getId());
                        break;
                    default:
                        break;
                }
                tasksManager.lastId = maxId;
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        return tasksManager;
    }

    /**
     * Добавление задачи в менеджер задач и в список истории просмотров
     *
     * @param task        Задача, которая наследуется от {@link Task}
     * @param line        csv строка
     * @param tasksMap    Хеш-таблица с задачами
     * @param taskManager Менеджер задач
     * @param history     Список истории просмотров
     */
    private static <T extends Task> void addTaskToManagerAndHistory(T task, String line, Map<Integer, T> tasksMap,
                                                                    FileBackedTasksManager taskManager,
                                                                    List<Integer> history) {
        task.fromScsString(line);
        tasksMap.put(task.getId(), task);
        if (history.contains(task.getId())) {
            taskManager.inMemoryHistoryManager.add(task);
        }
    }

    /**
     * Сохранение данных объекта в файл
     */
    private void save() {

        try (Writer fileWriter = new FileWriter(this.path.toFile())) {

            fileWriter.write("id,type,name,status,description,epic\n");
            for (var task : super.getTasks()) {
                fileWriter.write(task.toCsvString() + "\n");
            }

            for (var epic : super.getEpics()) {
                fileWriter.write(epic.toCsvString() + "\n");
            }

            for (var sub : super.getSubtasks()) {
                fileWriter.write(sub.toCsvString() + "\n");
            }

            fileWriter.write("\n");

            fileWriter.write(historyToString(this.inMemoryHistoryManager));
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    @Override
    public int addTask(Task task) {
        var parentResult = super.addTask(task);
        save();
        return parentResult;
    }

    @Override
    public int addEpic(Epic epic) {
        var parentResult = super.addEpic(epic);
        save();
        return parentResult;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        var parentResult = super.updateEpic(epic);
        save();
        return parentResult;
    }

    @Override
    public boolean deleteEpicById(int epicId) {
        var parentResult = super.deleteEpicById(epicId);
        save();
        return parentResult;
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public boolean clearSubtasksInEpic(int epicId) {
        var parentResult = super.clearSubtasksInEpic(epicId);
        save();
        return parentResult;
    }

    @Override
    public int addSubtaskToEpic(Subtask subtask) {
        var parentResult = super.addSubtaskToEpic(subtask);
        save();
        return parentResult;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        var parentResult = super.updateSubtask(subtask);
        save();
        return parentResult;
    }

    @Override
    public boolean deleteSubtaskById(int subtaskId) {
        var parentResult = super.deleteSubtaskById(subtaskId);
        save();
        return parentResult;
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public boolean updateTask(Task task) {
        var parentResult = super.updateTask(task);
        save();
        return parentResult;
    }

    @Override
    public boolean deleteTask(int taskId) {
        var parentResult = super.deleteTask(taskId);
        save();
        return parentResult;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    /**
     * Конвертация истории промотра из менеджера в строку
     *
     * @param manager Менеджер истории
     * @return Строка
     */
    static String historyToString(HistoryManager manager) {
        var listOfTasks = manager.getHistory();

        StringBuilder resultHistoryString = new StringBuilder();
        for (int i = 0; i < listOfTasks.size(); i++) {
            resultHistoryString.append(listOfTasks.get(i).getId());
            if (i != listOfTasks.size() - 1) {
                resultHistoryString.append(",");
            }
        }
        return resultHistoryString.toString();
    }

    /**
     * Конвертация истории промотра из строки в список
     *
     * @param value Строка
     * @return Список
     */
    static List<Integer> historyFromString(String value) {
        if (value.equals("")) {
            return Collections.emptyList();
        }
        List<Integer> listOfIds = new ArrayList<>();

        String[] split = value.split(",");

        for (String item : split) {
            if (item.isEmpty()) {
                return Collections.emptyList();
            }
            try {
                int id = Integer.parseInt(item);
                listOfIds.add(id);
            } catch (NumberFormatException e) {
                return Collections.emptyList();
            }
        }

        return listOfIds;
    }

    @Override
    public Epic getEpicById(int epicId) {
        var parentResult = super.getEpicById(epicId);
        save();
        return parentResult;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        var parentResult = super.getSubtaskById(subtaskId);
        save();
        return parentResult;
    }

    @Override
    public Task getTaskById(int taskId) {
        var parentResult = super.getTaskById(taskId);
        save();
        return parentResult;
    }

    public Path getPath() {
        return path;
    }
}
