package kanban.manager;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Objects;

import kanban.models.*;

/**
 * Менеджер по управлению классами
 */
public class TaskManager {
    private int lastId = 0;
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Task> tasks = new HashMap<>();


    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Добавление нового {@link Epic}
     *
     * @param epic Объект для добавления
     * @return Присвоенный ID
     */
    public int addEpic(Epic epic) {
        int newId = ++lastId;
        epic.setId(newId);
        epics.put(newId, epic);
        return newId;
    }

    /**
     * Обновление {@link Epic}
     *
     * @param epic Объект для обновления
     * @return Результат обновления
     */
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

    /**
     * Удаление {@link Epic}
     *
     * @param epicId Id объекта
     * @return Результат удаления
     */
    public boolean deleteEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            var subtasks = epics.get(epicId).getSubtasks();
            for (Integer subtask : subtasks) {
                this.subtasks.remove(subtask);
            }
            epics.get(epicId).getSubtasks().clear();
            epics.remove(epicId);
            return true;
        }
        return false;
    }

    /**
     * Полное удаление всех {@link Epic}
     */
    public void clearEpics() {
        subtasks.clear();
        epics.clear();
    }

    /**
     * Получить {@link Epic} по ID
     *
     * @param epicId Id объекта
     * @return {@link Epic}, null - если не найден
     */
    public Epic getEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            return new Epic(epics.get(epicId));
        }
        return null;
    }

    /**
     * Удаление {@link Subtask} из {@link Epic}
     *
     * @param epicId Id объекта
     * @return Результат удаления
     */
    public boolean clearSubtasksInEpic(int epicId) {
        var epic = epics.get(epicId);

        if (epic == null) {
            return false;
        }

        for (int subtask : epic.getSubtasks()) {
            subtasks.remove(subtask);
        }

        epic.getSubtasks().clear();
        setEpicStatus(epicId);

        return true;
    }

    /**
     * Получить все {@link Subtask} из {@link Epic}
     *
     * @param epicId Id объекта
     * @return список
     */
    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        ArrayList<Subtask> subtasks = new ArrayList<>();

        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);

            for (int subtaskId : epic.getSubtasks()) {
                subtasks.add(this.subtasks.get(subtaskId));
            }
        }

        return subtasks;
    }

    /**
     * Добавление {@link Subtask} в {@link Epic}
     *
     * @param subtask объект для добавления
     * @return Присвоенный ID
     */
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

    /**
     * Обновление {@link Subtask}
     *
     * @param subtask Объект для обновления
     * @return Результат обновления
     */
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

    /**
     * Удаление {@link Subtask}
     *
     * @param subtaskId Id объекта
     * @return Результат удаления
     */
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
        epic.getSubtasks().remove((Integer) subtaskId);

        setEpicStatus(epicId);

        return true;
    }

    /**
     * Полное удаление всех {@link Subtask}
     */
    public void clearSubtasks() {
        if (subtasks.values().size() == 0) {
            return;
        }

        // Если тут не объявить копию коллекции, то при переборе выдаст ConcurrentModificationException
        var subtaskValues = new HashMap<Integer, Subtask>(subtasks);

        for (var subtask : subtaskValues.values()) {
            deleteSubtaskById(subtask.getId());
        }
    }

    /**
     * Получить {@link Subtask} по ID
     *
     * @param subtaskId Id объекта
     * @return {@link Subtask}, null - если не найден
     */
    public Subtask getSubtaskById(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            return new Subtask(subtasks.get(subtaskId));
        }
        return null;
    }

    /**
     * Проверка статусов всех {@link Subtask} в {@link Epic}
     * @param epicId Id объекта
     * @return Статус
     */
    private SubtasksStatus checkEpicForStatus(int epicId) {
        ArrayList<Subtask> subtasksByEpic = getSubtasksByEpic(epicId);
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

//        if (isSubtasksDone(epicId)) {
//            epics.get(epicId).setStatus(TaskStatus.DONE);
//        } else if (isEpicInProgress(epicId)) {
//            epics.get(epicId).setStatus(TaskStatus.IN_PROGRESS);
//        } else {
//            epics.get(epicId).setStatus(TaskStatus.NEW);
//        }
    }

    /**
     * Добавление нового {@link Task}
     *
     * @param task Объект для добавления
     * @return Присвоенный ID
     */
    public int addTask(Task task) {
        int newId = ++lastId;
        task.setId(newId);
        tasks.put(newId, task);
        return newId;
    }

    /**
     * Обновление {@link Task}
     *
     * @param task Объект для обновления
     * @return Результат обновления
     */
    public boolean updateTask(Task task) {
        int taskId = task.getId();

        if (tasks.containsKey(taskId)) {
            tasks.put(taskId, task);
            return true;
        }
        return false;
    }

    /**
     * Удаление {@link Task}
     *
     * @param taskId Id объекта
     * @return Результат удаления
     */
    public boolean deleteTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            return true;
        }
        return false;
    }

    /**
     * Полное удаление всех {@link Task}
     */
    public void clearTasks() {
        tasks.clear();
    }

    public Task getTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            return new Task(tasks.get(taskId));
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskManager that = (TaskManager) o;
        return lastId == that.lastId && Objects.equals(epics, that.epics) && Objects.equals(subtasks, that.subtasks) && Objects.equals(tasks, that.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastId, epics, subtasks, tasks);
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                "lastId=" + lastId +
                ", epics.size()=" + epics.size() +
                ", subtasks.size()=" + subtasks.size() +
                ", tasks.size()=" + tasks.size() +
                '}';
    }
}
