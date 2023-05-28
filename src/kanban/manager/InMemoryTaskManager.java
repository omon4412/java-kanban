package kanban.manager;

import java.time.Instant;
import java.util.*;

import kanban.models.*;

/**
 * Менеджер по управлению задачами
 */
public class InMemoryTaskManager implements TaskManager {
    protected int lastId = 0;
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    protected Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        prioritizedTasks = new TreeSet<>((task1, task2) -> {
            Instant instant1 = task1.getStartTime();
            Instant instant2 = task2.getStartTime();

            if (instant1 == null && instant2 == null) {
                return 0;
            } else if (instant1 == null) {
                return 1;
            } else if (instant2 == null) {
                return -1;
            } else {
                return instant1.compareTo(instant2);
            }
        });
    }

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
            if (epic1.getDuration() != epic.getDuration()) { // вручную продолжительность поменять нельзя
                return false;
            }
            if (epic1.getStartTime() != epic.getStartTime()) { // вручную начало поменять нельзя
                return false;
            }
            if (epic1.getEndTime() != epic.getEndTime()) { // вручную конец поменять нельзя
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

        getSubtasksByEpic(epicId).forEach(prioritizedTasks::remove);

        for (int subtask : epic.getSubtasks()) {
            subtasks.remove(subtask);
            inMemoryHistoryManager.remove(subtask);
        }

        epic.getSubtasks().clear();
        setEpicStatus(epicId);
        setEpicDuration(epicId);

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
        setEpicDuration(epicId);
        prioritizedTasks.add(subtask);

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
        setEpicDuration(epicId);

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
        prioritizedTasks.remove(subtask);
        subtasks.remove(subtaskId);
        inMemoryHistoryManager.remove(subtaskId);
        epic.getSubtasks().remove((Integer) subtaskId);

        setEpicStatus(epicId);
        setEpicDuration(epicId);

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
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();

        for (var epic : epics.values()) {
            epic.getSubtasks().clear();
            setEpicStatus(epic.getId());
            setEpicDuration(epic.getId());
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
            return SubtasksStatus.NONE_SUBTASKS_DONE_OR_IN_PROGRESS;
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

    /**
     * Установка продолжительности для {@link Epic}
     *
     * @param epicId Id объекта
     */
    private void setEpicDuration(int epicId) {
        final List<Subtask> subtasksByEpic = getSubtasksByEpic(epicId);

        if (subtasksByEpic.size() == 0) {
            epics.get(epicId).setStartTime(null);
            epics.get(epicId).setEndTime(null);
            epics.get(epicId).setDuration(0);
        }

        long sumDuration = subtasksByEpic.stream().mapToLong(Subtask::getDuration).sum();
        epics.get(epicId).setDuration(sumDuration);

        Optional<Instant> minInstant = subtasksByEpic.stream().filter(sub -> sub.getStatus() != TaskStatus.DONE)
                .map(Subtask::getStartTime).filter(Objects::nonNull).min(Instant::compareTo);
        Optional<Instant> maxInstant = subtasksByEpic.stream().filter(sub -> sub.getStatus() != TaskStatus.DONE)
                .map(Subtask::getEndTime).filter(Objects::nonNull).max(Instant::compareTo);

        epics.get(epicId).setStartTime(minInstant.orElse(null));
        epics.get(epicId).setEndTime(maxInstant.orElse(null));
    }

    @Override
    public int addTask(Task task) {
        int newId = ++lastId;
        task.setId(newId);
        tasks.put(newId, task);
        prioritizedTasks.add(task);
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
            prioritizedTasks.remove(tasks.get(taskId));
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
        prioritizedTasks.removeAll(tasks.values());
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
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public void printPrioritizedTasks() {
        prioritizedTasks.forEach(System.out::println);
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
