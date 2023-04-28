package kanban.manager;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kanban.models.*;

/**
 * Менеджер по управлению задачами
 */
public class InMemoryTaskManager implements TaskManager {
    private int lastId = 0;
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Task> tasks = new HashMap<>();

    private HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public int addEpic(Epic epic) {
        int newId = ++lastId;
        epic.setId(newId);
        epics.put(newId, epic);
        return newId;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        int epicId = epic.getId();

        if (epics.containsKey(epicId)) {
            var epic1 = epics.get(epicId);
            if (epic1.getStatus() != epic.getStatus()) { // вручную статус поменять нельзя
                return false;
            }
            epics.put(epicId, epic);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            var subtasks = epics.get(epicId).getSubtasks();
            for (Integer subtask : subtasks) {
                this.subtasks.remove(subtask);
                inMemoryHistoryManager.remove(subtask);
            }
            epics.get(epicId).getSubtasks().clear();
            epics.remove(epicId);
            inMemoryHistoryManager.remove(epicId);
            return true;
        }
        return false;
    }

    @Override
    public void clearEpics() {
        for (Integer subtask : subtasks.keySet()) {
            inMemoryHistoryManager.remove(subtask);
        }
        subtasks.clear();
        for (Integer epic : epics.keySet()) {
            inMemoryHistoryManager.remove(epic);
        }
        epics.clear();
    }

    @Override
    public Epic getEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            inMemoryHistoryManager.add(epic);
            return new Epic(epic);
        }
        return null;
    }

    @Override
    public boolean clearSubtasksInEpic(int epicId) {
        var epic = epics.get(epicId);

        if (epic == null) {
            return false;
        }

        for (int subtask : epic.getSubtasks()) {
            subtasks.remove(subtask);
            inMemoryHistoryManager.remove(subtask);
        }

        epic.getSubtasks().clear();
        setEpicStatus(epicId);

        return true;
    }

    @Override
    public List<Subtask> getSubtasksByEpic(int epicId) {
        List<Subtask> subtasks = new ArrayList<>();

        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);

            for (int subtaskId : epic.getSubtasks()) {
                subtasks.add(this.subtasks.get(subtaskId));
            }
        }

        return subtasks;
    }

    @Override
    public int addSubtaskToEpic(Subtask subtask) {
        int epicId = subtask.getEpicId();
        var epic = epics.get(epicId);
        if (epic == null) {
            return -1;
        }
        int newId = ++lastId;
        subtask.setId(newId);
        subtasks.put(newId, subtask);
        epic.getSubtasks().add(newId);

        setEpicStatus(epicId);

        return newId;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();
        if (!subtasks.containsKey(subtaskId)) {
            return false;
        }
        subtasks.put(subtaskId, subtask);

        int epicId = subtask.getEpicId();
        if (epicId == -1) {
            return false;
        }

        setEpicStatus(epicId);

        return true;
    }

    @Override
    public boolean deleteSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);

        if (subtask == null) {
            return false;
        }

        int epicId = subtask.getEpicId();

        var epic = epics.get(epicId);
        if (epic == null) {
            return false;
        }
        if (!epic.getSubtasks().contains(subtaskId)) {
            return false;
        }
        if (!subtasks.containsKey(subtaskId)) {
            return false;
        }
        subtasks.remove(subtaskId);
        inMemoryHistoryManager.remove(subtaskId);
        epic.getSubtasks().remove((Integer) subtaskId);

        setEpicStatus(epicId);

        return true;
    }

    @Override
    public void clearSubtasks() {
        if (subtasks.values().size() == 0) {
            return;
        }

        for (Integer subtask : subtasks.keySet()) {
            inMemoryHistoryManager.remove(subtask);
        }
        subtasks.clear();

        for (var epic : epics.values()) {
            epic.getSubtasks().clear();
        }
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            Subtask subtask = subtasks.get(subtaskId);
            inMemoryHistoryManager.add(subtask);
            return new Subtask(subtask);
        }
        return null;
    }

    /**
     * Проверка статусов всех {@link Subtask} в {@link Epic}
     *
     * @param epicId Id объекта
     * @return Статус
     */
    private SubtasksStatus checkEpicForStatus(int epicId) {
        List<Subtask> subtasksByEpic = getSubtasksByEpic(epicId);
        int subtaskCount = subtasksByEpic.size();
        int subtaskDoneCount = 0;
        int subtaskInProgressCount = 0;

        if (subtaskCount == 0) {
            return SubtasksStatus.SUBTASKS_DONE;
        }

        for (Subtask subtask : subtasksByEpic) {
            if (subtask.getStatus() == TaskStatus.DONE) {
                subtaskDoneCount++;
            }
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                subtaskInProgressCount++;
            }
        }

        if (subtaskDoneCount == subtaskCount) {
            return SubtasksStatus.SUBTASKS_DONE;
        } else if (subtaskInProgressCount >= 1 || subtaskDoneCount >= 1) {
            return SubtasksStatus.ONE_IN_PROGRESS;
        } else {
            return SubtasksStatus.NONE_SUBTASKS_DONE_OR_IN_PROGRESS;
        }
    }

    /**
     * Установка статуса для {@link Epic}
     *
     * @param epicId Id объекта
     */
    private void setEpicStatus(int epicId) {

        switch (checkEpicForStatus(epicId)) {
            case SUBTASKS_DONE:
                epics.get(epicId).setStatus(TaskStatus.DONE);
                break;
            case ONE_IN_PROGRESS:
                epics.get(epicId).setStatus(TaskStatus.IN_PROGRESS);
                break;
            case NONE_SUBTASKS_DONE_OR_IN_PROGRESS:
                epics.get(epicId).setStatus(TaskStatus.NEW);
                break;
        }
    }

    @Override
    public int addTask(Task task) {
        int newId = ++lastId;
        task.setId(newId);
        tasks.put(newId, task);
        return newId;
    }

    @Override
    public boolean updateTask(Task task) {
        int taskId = task.getId();

        if (tasks.containsKey(taskId)) {
            tasks.put(taskId, task);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            inMemoryHistoryManager.remove(taskId);
            return true;
        }
        return false;
    }

    @Override
    public void clearTasks() {
        for (Integer task : tasks.keySet()) {
            inMemoryHistoryManager.remove(task);
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            Task task = tasks.get(taskId);
            inMemoryHistoryManager.add(task);
            return new Task(task);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager that = (InMemoryTaskManager) o;
        return lastId == that.lastId && Objects.equals(epics, that.epics) && Objects.equals(subtasks, that.subtasks) && Objects.equals(tasks, that.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastId, epics, subtasks, tasks);
    }

    @Override
    public String toString() {
        return "InMemoryTaskManager{" +
                "lastId=" + lastId +
                ", epics.size()=" + epics.size() +
                ", subtasks.size()=" + subtasks.size() +
                ", tasks.size()=" + tasks.size() +
                '}';
    }
}
