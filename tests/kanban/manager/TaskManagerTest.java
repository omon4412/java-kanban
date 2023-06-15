package kanban.manager;

import kanban.models.Epic;
import kanban.models.Subtask;
import kanban.models.Task;
import kanban.models.TaskStatus;
import kanban.util.MathConsts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {

    public T manager;
    public Task task;
    public Epic epic;
    public Subtask subtask;

    public abstract T createManager();

    @BeforeEach
    public void loadManager() {
        manager = createManager();
    }

    @BeforeEach
    public void createTaskEpicSubtask() {
        task = new Task("Task name", "Task desc", TaskStatus.NEW);
        epic = new Epic("Epic name", "Epic desc");
        subtask = new Subtask("Sub name", "Sub desc", TaskStatus.NEW);
    }

    @Test
    public void shouldAddTaskAndReturnOne() {
        int taskId = manager.addTask(task);
        assertEquals(1, taskId);
        assertNotNull(manager.getTaskById(1));
    }

    @Test
    public void shouldAddTaskAndReturnTwo() {
        manager.addTask(new Task());
        int taskId = manager.addTask(task);
        assertEquals(2, taskId);
        assertNotNull(manager.getTaskById(2));
    }

    @Test
    public void shouldUpdateTask() {
        manager.addTask(task);
        String newName = "New name";
        String newDesc = "New desc";
        TaskStatus newStatus = TaskStatus.DONE;
        Task task1 = new Task(newName, newDesc, newStatus);
        task1.setId(task.getId());
        boolean res = manager.updateTask(task1);

        assertTrue(res, "Ошибка обновления задачи");
        assertEquals(newName, manager.getTaskById(task.getId()).getName(), "Имя не обновлено");
        assertEquals(newStatus, manager.getTaskById(task.getId()).getStatus(), "Статус не обновлён");
    }

    @Test
    public void shouldNotUpdateTask() {
        manager.addTask(task);
        String newName = "New name";
        String newDesc = "New desc";
        TaskStatus newStatus = TaskStatus.DONE;
        Task task1 = new Task(newName, newDesc, newStatus);
        task1.setId(777);
        boolean res = manager.updateTask(task1);

        assertFalse(res);
    }

    @Test
    public void shouldDeleteTaskAndRemainZeroTasks() {
        int taskId = manager.addTask(task);
        boolean res = manager.deleteTask(taskId);

        assertTrue(res, "Ошибка удаления задачи");
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    public void shouldReturnFalseAfterDeleteTask() {
        boolean res = manager.deleteTask(777);
        assertFalse(res, "Задача не должна удаляться");
    }

    @Test
    public void shouldDeleteTaskAndRemainOneTasks() {
        int taskId = manager.addTask(task);
        manager.addTask(new Task());
        boolean res = manager.deleteTask(taskId);

        assertTrue(res, "Ошибка удаления задачи");
        assertEquals(1, manager.getTasks().size());
    }

    @Test
    public void shouldDeleteAllTask() {
        manager.addTask(new Task());
        manager.addTask(new Task());
        manager.addTask(new Task());
        manager.clearTasks();

        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    public void shouldReturnTask() {
        int taskId = manager.addTask(task);
        var res = manager.getTaskById(taskId);

        assertNotNull(res, "Задача не найдена");
        assertEquals(taskId, res.getId());
    }

    @Test
    public void shouldReturnNullTask() {
        var res = manager.getTaskById(777);
        assertNull(res);
    }

    @Test
    public void shouldReturnListOfTask() {
        manager.addTask(task);
        Task task1 = new Task();
        Task task2 = new Task();
        manager.addTask(task1);
        manager.addTask(task2);
        var res = manager.getTasks();
        List<Task> testList = new ArrayList<>() {{
            add(task);
            add(task1);
            add(task2);
        }};
        assertEquals(testList, res);
    }

    ////////////////////////////////////
    @Test
    public void shouldAddEpicAndReturnOne() {
        int epicId = manager.addEpic(epic);
        assertEquals(1, epicId);
        assertNotNull(manager.getEpicById(1));
    }

    @Test
    public void shouldAddEpicAndReturnTwo() {
        manager.addEpic(new Epic());
        int epicId = manager.addEpic(epic);
        assertEquals(2, epicId);
        assertNotNull(manager.getEpicById(2));
    }

    @Test
    public void shouldUpdateEpic() {
        manager.addEpic(epic);
        String newName = "New name";
        String newDesc = "New desc";
        Epic epic1 = new Epic(newName, newDesc);
        epic1.setId(epic.getId());
        boolean res = manager.updateEpic(epic1);

        assertTrue(res, "Ошибка обновления эпика");
        assertEquals(newName, manager.getEpicById(epic.getId()).getName(), "Имя не обновлено");
        assertEquals(newDesc, manager.getEpicById(epic.getId()).getDescription(), "Статус не обновлён");
    }

    @Test
    public void shouldNotUpdateEpic() {
        manager.addEpic(epic);
        String newName = "New name";
        String newDesc = "New desc";
        Epic epic1 = new Epic(newName, newDesc);
        epic1.setId(777);
        boolean res = manager.updateEpic(epic1);

        assertFalse(res);
    }

    @Test
    public void shouldDeleteEpicAndRemainZeroEpics() {
        int epicId = manager.addEpic(epic);
        boolean res = manager.deleteEpicById(epicId);

        assertTrue(res, "Ошибка удаления задачи");
        assertEquals(0, manager.getEpics().size());
    }

    @Test
    public void shouldReturnFalseAfterDeleteEpic() {
        boolean res = manager.deleteEpicById(777);
        assertFalse(res, "Задача не должна удаляться");
    }

    @Test
    public void shouldDeleteEpicAndRemainOneEpic() {
        int epicId = manager.addEpic(epic);
        manager.addEpic(new Epic());
        boolean res = manager.deleteEpicById(epicId);

        assertTrue(res, "Ошибка удаления задачи");
        assertEquals(1, manager.getEpics().size());
    }

    @Test
    public void shouldDeleteAllEpic() {
        manager.addEpic(new Epic());
        manager.addEpic(new Epic());
        manager.addEpic(new Epic());
        manager.clearEpics();

        assertTrue(manager.getEpics().isEmpty());
    }

    @Test
    public void shouldReturnEpic() {
        int epicId = manager.addEpic(epic);
        var res = manager.getEpicById(epicId);

        assertNotNull(res, "Задача не найдена");
        assertEquals(epicId, res.getId());
    }

    @Test
    public void shouldReturnNullEpic() {
        var res = manager.getEpicById(777);
        assertNull(res);
    }

    @Test
    public void shouldReturnListOfEpic() {
        manager.addEpic(epic);
        Epic epic1 = new Epic();
        Epic epic2 = new Epic();
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        var res = manager.getEpics();
        List<Epic> testList = new ArrayList<>() {{
            add(epic);
            add(epic1);
            add(epic2);
        }};
        assertEquals(testList, res);
    }

    /////////////////////
    @Test
    public void shouldAddSubtaskAndReturnTwo() {
        int epicId = manager.addEpic(epic);
        subtask.setEpicId(epicId);
        int subId = manager.addSubtaskToEpic(subtask);
        assertEquals(2, subId);
        assertNotNull(manager.getSubtaskById(2));
    }

    @Test
    public void shouldAddSubtaskAndReturnTree() {
        int epicId = manager.addEpic(epic);
        Subtask subtask1 = new Subtask("test");
        subtask1.setEpicId(epicId);
        subtask.setEpicId(epicId);

        manager.addSubtaskToEpic(subtask1);
        int taskId = manager.addSubtaskToEpic(subtask);
        assertEquals(3, taskId);
        assertNotNull(manager.getSubtaskById(3));
    }

    @Test
    public void shouldNotAddSubtask() {
        int subId = manager.addSubtaskToEpic(subtask);
        assertEquals(-1, subId);
    }

    @Test
    public void shouldUpdateSubtask() {
        int epicId = manager.addEpic(epic);
        subtask.setEpicId(epicId);
        manager.addSubtaskToEpic(subtask);

        String newName = "New name";
        String newDesc = "New desc";
        TaskStatus newStatus = TaskStatus.DONE;
        Subtask sub1 = new Subtask(newName, newDesc, newStatus);

        sub1.setId(subtask.getId());
        sub1.setEpicId(subtask.getEpicId());
        boolean res = manager.updateSubtask(sub1);

        assertTrue(res, "Ошибка обновления подзадачи");
        assertEquals(newName, manager.getSubtaskById(subtask.getId()).getName(), "Имя не обновлено");
        assertEquals(newStatus, manager.getSubtaskById(subtask.getId()).getStatus(), "Статус не обновлён");
        assertEquals(newDesc, manager.getSubtaskById(subtask.getId()).getDescription(), "Описание не обновлено");
    }

    @Test
    public void shouldNotUpdateSubtask() {
        int epicId = manager.addEpic(epic);
        subtask.setEpicId(epicId);
        manager.addSubtaskToEpic(subtask);
        String newName = "New name";
        Subtask task1 = new Subtask(newName);
        task1.setId(777);
        boolean res = manager.updateSubtask(task1);

        assertFalse(res);
    }

    @Test
    public void shouldDeleteSubtaskAndRemainZeroSubtasks() {
        int epicId = manager.addEpic(epic);
        subtask.setEpicId(epicId);
        int taskId = manager.addSubtaskToEpic(subtask);
        boolean res = manager.deleteSubtaskById(taskId);

        assertTrue(res, "Ошибка удаления задачи");
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    public void shouldReturnFalseAfterDeleteSubtask() {
        boolean res = manager.deleteSubtaskById(777);
        assertFalse(res, "Задача не должна удаляться");
    }

    @Test
    public void shouldDeleteSubtaskAndRemainOneSubtasks() {
        int epicId = manager.addEpic(epic);
        subtask.setEpicId(epicId);
        int taskId = manager.addSubtaskToEpic(subtask);
        Subtask subtask1 = new Subtask("test");
        subtask1.setEpicId(epicId);
        manager.addSubtaskToEpic(subtask1);
        boolean res = manager.deleteSubtaskById(taskId);

        assertTrue(res, "Ошибка удаления задачи");
        assertEquals(1, manager.getSubtasks().size());
    }

    @Test
    public void shouldDeleteAllSubtask() {
        int epicId = manager.addEpic(epic);
        Subtask subtask1 = new Subtask("test");
        Subtask subtask2 = new Subtask("test");
        Subtask subtask3 = new Subtask("test");
        subtask1.setEpicId(epicId);
        subtask2.setEpicId(epicId);
        subtask3.setEpicId(epicId);
        manager.addSubtaskToEpic(subtask1);
        manager.addSubtaskToEpic(subtask2);
        manager.addSubtaskToEpic(subtask3);
        manager.clearSubtasks();

        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    public void shouldReturnSubtask() {
        int epicId = manager.addEpic(epic);
        subtask.setEpicId(epicId);
        int taskId = manager.addSubtaskToEpic(subtask);
        var res = manager.getSubtaskById(taskId);

        assertNotNull(res, "Задача не найдена");
        assertEquals(taskId, res.getId());
    }

    @Test
    public void shouldReturnNullSubtask() {
        var res = manager.getSubtaskById(777);
        assertNull(res);
    }

    @Test
    public void shouldReturnListOfSubtask() {
        int epicId = manager.addEpic(epic);
        Subtask subtask1 = new Subtask("test");
        Subtask subtask2 = new Subtask("test");
        Subtask subtask3 = new Subtask("test");
        subtask1.setEpicId(epicId);
        subtask2.setEpicId(epicId);
        subtask3.setEpicId(epicId);
        manager.addSubtaskToEpic(subtask1);
        manager.addSubtaskToEpic(subtask2);
        manager.addSubtaskToEpic(subtask3);
        var res = manager.getSubtasks();
        List<Subtask> testList = new ArrayList<>() {{
            add(subtask1);
            add(subtask2);
            add(subtask3);
        }};
        assertEquals(testList, res);
    }

    @Test
    public void createEpicAndEpicStatusShouldNew() {
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void createEpicAndSubAndEpicStatusShouldNew() {
        createEpicThreeSub(TaskStatus.NEW, TaskStatus.NEW, TaskStatus.NEW);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void createEpicAndSubAndEpicStatusShouldDone() {
        createEpicThreeSub(TaskStatus.DONE, TaskStatus.DONE, TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void createEpicAndSubAndAfterClearEpicStatusShouldNew() {
        createEpicThreeSub(TaskStatus.DONE, TaskStatus.DONE, TaskStatus.DONE);
        manager.clearSubtasks();
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void createEpicAndSubAndEpicStatusShouldInProgress() {
        createEpicThreeSub(TaskStatus.DONE, TaskStatus.NEW, TaskStatus.NEW);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void createEpicAndSubAndEpicStatusShouldInProgress2() {
        createEpicThreeSub(TaskStatus.IN_PROGRESS, TaskStatus.IN_PROGRESS, TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    public void createEpicThreeSub(TaskStatus ts1, TaskStatus ts2, TaskStatus ts3) {
        Subtask subtask1 = new Subtask("test", ts1);
        Subtask subtask2 = new Subtask("test", ts2);
        Subtask subtask3 = new Subtask("test", ts3);
        manager.addEpic(epic);
        subtask1.setEpicId(epic.getId());
        subtask2.setEpicId(epic.getId());
        subtask3.setEpicId(epic.getId());
        manager.addSubtaskToEpic(subtask1);
        manager.addSubtaskToEpic(subtask2);
        manager.addSubtaskToEpic(subtask3);
    }

    @Test
    public void taskShouldBeNullAndZero() {
        Task task1 = new Task("test", "desc", TaskStatus.DONE);
        assertNull(task1.getStartTime());
        assertEquals(0, task1.getDuration());
    }

    @Test
    public void taskEndTimeShouldBeNull() {
        Task task1 = new Task("test", "desc", TaskStatus.DONE);
        assertNull(task1.getEndTime());
    }

    @Test
    public void taskStartTimeShouldBeNotNullDurationIs60EndTimeNotNull() {
        Instant instant = Instant.now();
        Task task1 = new Task("test", "desc", TaskStatus.DONE, instant, 60);
        assertNotNull(task1.getStartTime());
        assertNotNull(task1.getEndTime());
        assertEquals(60, task1.getDuration());
        assertEquals(instant.plusMillis(60 * 60 * 1000), task1.getEndTime());
    }

    @Test
    public void subShouldBeNullAndZero() {
        Subtask subtask1 = new Subtask("test", "desc", TaskStatus.DONE);
        assertNull(subtask1.getStartTime());
        assertEquals(0, subtask1.getDuration());
    }

    @Test
    public void subStartTimeShouldBeNotNullDurationIs100EndTimeNotNull() {
        Instant instant = Instant.now();
        Subtask task1 = new Subtask("test", "desc", TaskStatus.IN_PROGRESS, instant, 100);
        assertNotNull(task1.getStartTime());
        assertNotNull(task1.getEndTime());
        assertEquals(100, task1.getDuration());
        assertEquals(instant.plusMillis(100 * 60 * 1000), task1.getEndTime());
    }

    @Test
    public void epicDurationShouldBe11() {
        Instant now = Instant.now();
        Subtask subtask1 = new Subtask("test", "desc", TaskStatus.NEW, now, 1);
        Subtask subtask2 = new Subtask("test", "desc", TaskStatus.NEW,
                now.plusMillis(1000000000L), 10);
        manager.addEpic(epic);
        subtask1.setEpicId(epic.getId());
        subtask2.setEpicId(epic.getId());
        manager.addSubtaskToEpic(subtask1);
        manager.addSubtaskToEpic(subtask2);
        assertEquals(11, epic.getDuration());
    }

    @Test
    public void shouldSetStartAndEndTimeToEpicIfSubNew() {
        Instant now1 = Instant.now().plusMillis(5000);
        Instant now2 = Instant.now().plusMillis(1000000000);
        Subtask subtask1 = new Subtask("test", "desc", TaskStatus.NEW, now1, 25);
        Subtask subtask2 = new Subtask("test", "desc", TaskStatus.IN_PROGRESS, now2, 60);
        manager.addEpic(epic);
        subtask1.setEpicId(epic.getId());
        subtask2.setEpicId(epic.getId());
        manager.addSubtaskToEpic(subtask1);
        manager.addSubtaskToEpic(subtask2);

        assertEquals(85, epic.getDuration());
        assertEquals(now1, epic.getStartTime());
        assertEquals(now2.plusMillis(subtask2.getDuration() * 60 * 1000), epic.getEndTime());
    }

    @Test
    public void shouldSetStartAndEndTimeToEpicIfOneSubDone() {
        Instant now1 = Instant.now().plusMillis(5000);
        Instant now2 = Instant.now().plusMillis(10000000000L);
        Subtask subtask1 = new Subtask("test", "desc", TaskStatus.NEW, now1, 25);
        Subtask subtask2 = new Subtask("test", "desc", TaskStatus.DONE, now2, 60);
        manager.addEpic(epic);
        subtask1.setEpicId(epic.getId());
        subtask2.setEpicId(epic.getId());
        manager.addSubtaskToEpic(subtask1);
        manager.addSubtaskToEpic(subtask2);

        assertEquals(85, epic.getDuration());
        assertEquals(now1, epic.getStartTime());
        assertEquals(now1.plusMillis(subtask1.getDuration() * 60 * 1000), epic.getEndTime());
    }

    @Test
    public void shouldSetNullToStartAndEndTimeToEpic() {
        Instant now1 = Instant.now().plusMillis(5000);
        Instant now2 = Instant.now().plusMillis(100000000);
        Subtask subtask1 = new Subtask("test", "desc", TaskStatus.NEW, now1, 25);
        Subtask subtask2 = new Subtask("test", "desc", TaskStatus.DONE, now2, 60);
        manager.addEpic(epic);
        subtask1.setEpicId(epic.getId());
        subtask2.setEpicId(epic.getId());
        manager.addSubtaskToEpic(subtask1);
        manager.addSubtaskToEpic(subtask2);
        manager.clearSubtasks();
        assertEquals(0, epic.getDuration());
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
    }

    @Test
    public void shouldRemoveSubtasksFromPrioritizedTasksTree() {
        Instant now1 = Instant.now().plusMillis(5000);
        Instant now2 = Instant.now().plusMillis(1000000000);
        Subtask subtask1 = new Subtask("test", "desc", TaskStatus.NEW, now1, 25);
        Subtask subtask2 = new Subtask("test", "desc", TaskStatus.NEW, now2, 60);
        manager.addEpic(epic);
        subtask1.setEpicId(epic.getId());
        subtask2.setEpicId(epic.getId());
        manager.addSubtaskToEpic(subtask1);
        manager.addSubtaskToEpic(subtask2);

        assertEquals(2, manager.getPrioritizedTasks().size());

        manager.clearSubtasksInEpic(epic.getId());

        assertTrue(manager.getPrioritizedTasks().isEmpty());
    }

    @Test
    public void shouldRemoveSubtasksFromPrioritizedTasksTreeAndRemainOne() {
        Instant now1 = Instant.now().plusMillis(5000);
        Instant now2 = Instant.now().plusMillis(100000000);
        Subtask subtask1 = new Subtask("test", "desc", TaskStatus.NEW, now1, 25);
        Subtask subtask2 = new Subtask("test", "desc", TaskStatus.NEW, now2, 60);
        manager.addEpic(epic);
        subtask1.setEpicId(epic.getId());
        subtask2.setEpicId(epic.getId());
        manager.addSubtaskToEpic(subtask1);
        manager.addSubtaskToEpic(subtask2);
        manager.addTask(task);

        assertEquals(3, manager.getPrioritizedTasks().size());
        manager.clearSubtasks();
        assertEquals(1, manager.getPrioritizedTasks().size());
        assertEquals(task, manager.getPrioritizedTasks().stream().findFirst().get());
    }

    @Test
    public void shouldSortLike_Sub2_Sub1_Task() {
        manager.addTask(task); // third because null
        Instant now1 = Instant.now().plusMillis(250000000);
        Instant now2 = Instant.now().plusMillis(10000);
        Subtask subtask1 = new Subtask("test1", "desc", TaskStatus.NEW, now1, 25); // second
        Subtask subtask2 = new Subtask("test2", "desc", TaskStatus.NEW, now2, 60); // first
        manager.addEpic(epic);
        subtask1.setEpicId(epic.getId());
        subtask2.setEpicId(epic.getId());
        manager.addSubtaskToEpic(subtask1);
        manager.addSubtaskToEpic(subtask2);

        String expected = "[" + subtask2.getName() + ", " + subtask1.getName() + ", " + task.getName() + "]";
        assertEquals(expected, manager.getPrioritizedTasks().stream()
                .map(Task::getName).collect(Collectors.toList()).toString());
    }

    @Test
    public void createIntervalGridTest() {
        assertEquals(MathConsts.SECOND_IN_MINUTE * MathConsts.HOURS_IN_DAY
                        * MathConsts.DAYS_IN_YEAR / InMemoryTaskManager.MINUTES_INTERVAL,
                manager.getGridWithIntervals().size());
    }

    @Test
    public void shouldThrowIntersectException() {
        Instant now = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS);
        Instant now2 = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS * 10);
        Task task1 = new Task("test1", "", now, 20);
        Task task2 = new Task("test2", "", now2, 10);

        final IntersectionDetectedException exception = assertThrows(
                IntersectionDetectedException.class,
                () -> {
                    {
                        manager.addTask(task1);
                        manager.addTask(task2);
                    }
                });

        assertEquals("Пересечение между задачами", exception.getMessage());
    }

    @Test
    public void shouldSuccessAdded() {
        Instant now = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS);
        Instant now2 = now.plusMillis(MathConsts.MINUTE_IN_MILLIS * 100);
        Task task1 = new Task("test1", now, 20);
        Task task2 = new Task("test2", now2, 10);

        manager.addTask(task1);
        manager.addTask(task2);

        assertEquals(2, manager.getTasks().size());
    }

    @Test
    public void shouldDeleteOneTaskFromInterval() {
        Instant now = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS * 100);
        Instant now2 = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS * 200);
        Task task1 = new Task("test1", now, 20);
        Task task2 = new Task("test2", now2, 10);

        manager.addTask(task1);
        manager.addTask(task2);

        assertEquals(2, manager.getTasks().size());
        manager.deleteTask(task1.getId());
        assertTrue(manager.getGridWithIntervals().get(calculateStartInterval(task1)));
        assertFalse(manager.getGridWithIntervals().get(calculateStartInterval(task2)));
    }

    @Test
    public void shouldDeleteOneSubFromInterval() {
        Instant now = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS * 100);
        Instant now2 = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS * 200);
        Subtask subtask1 = new Subtask("test1", now, 20);
        Subtask subtask2 = new Subtask("test2", now2, 10);
        manager.addEpic(epic);
        subtask1.setEpicId(epic.getId());
        subtask2.setEpicId(epic.getId());
        manager.addSubtaskToEpic(subtask1);
        manager.addSubtaskToEpic(subtask2);

        assertEquals(2, manager.getSubtasks().size());
        manager.deleteSubtaskById(subtask1.getId());
        assertTrue(manager.getGridWithIntervals().get(calculateStartInterval(subtask1)));
        assertFalse(manager.getGridWithIntervals().get(calculateStartInterval(subtask2)));
    }

    @Test
    public void shouldClearTasksAndIntervals() {
        Instant now = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS * 100);
        Instant now2 = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS * 200);
        Task task1 = new Task("test1", now, 20);
        Task task2 = new Task("test2", now2, 10);

        manager.addTask(task1);
        manager.addTask(task2);

        assertEquals(2, manager.getTasks().size());
        manager.clearTasks();
        assertTrue(manager.getGridWithIntervals().get(calculateStartInterval(task1)));
        assertTrue(manager.getGridWithIntervals().get(calculateStartInterval(task2)));
    }

    @Test
    public void shouldClearSubsAndIntervals() {
        Instant now = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS * 100);
        Instant now2 = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS * 200);
        Subtask subtask1 = new Subtask("test1", now, 20);
        Subtask subtask2 = new Subtask("test2", now2, 10);
        manager.addEpic(epic);
        subtask1.setEpicId(epic.getId());
        subtask2.setEpicId(epic.getId());
        manager.addSubtaskToEpic(subtask1);
        manager.addSubtaskToEpic(subtask2);

        assertEquals(2, manager.getSubtasks().size());
        manager.clearSubtasks();
        assertTrue(manager.getGridWithIntervals().get(calculateStartInterval(subtask1)));
        assertTrue(manager.getGridWithIntervals().get(calculateStartInterval(subtask2)));
    }

    @Test
    public void shouldClearSubsAndIntervalsWithEpics() {
        Instant now = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS * 100);
        Instant now2 = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS * 200);
        Subtask subtask1 = new Subtask("test1", now, 20);
        Subtask subtask2 = new Subtask("test2", now2, 10);
        manager.addEpic(epic);
        subtask1.setEpicId(epic.getId());
        subtask2.setEpicId(epic.getId());
        manager.addSubtaskToEpic(subtask1);
        manager.addSubtaskToEpic(subtask2);

        assertEquals(2, manager.getSubtasks().size());
        manager.clearEpics();
        assertTrue(manager.getGridWithIntervals().get(calculateStartInterval(subtask1)));
        assertTrue(manager.getGridWithIntervals().get(calculateStartInterval(subtask2)));
    }

    @Test
    public void shouldThrowIntersectExceptionWhileUpdateTask() {
        Instant now = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS);
        Instant now2 = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS * 10);
        Task task1 = new Task("test1", "", now, 20);
        Task task2 = new Task("test2", "", now2, 10);
        Task task3 = new Task("test3", "", now, 20);

        final IntersectionDetectedException exception = assertThrows(
                IntersectionDetectedException.class,
                () -> {
                    {
                        manager.addTask(task1);
                        task3.setId(manager.addTask(task2));
                        manager.updateTask(task3);
                    }
                });

        assertEquals("Пересечение между задачами", exception.getMessage());
    }

    @Test
    public void shouldChangeIntervalWhileUpdateTask() {
        Instant now = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS);
        Instant now2 = Instant.now().plusMillis(MathConsts.MINUTE_IN_MILLIS * 10);
        Task task2 = new Task("test2", "", now2, 10);
        Task task3 = new Task("test3", "",
                now.plusMillis(MathConsts.MINUTE_IN_MILLIS * 100), 20);

        task3.setId(manager.addTask(task2));
        assertFalse(manager.getGridWithIntervals().get(calculateStartInterval(task2)));
        manager.updateTask(task3);
        assertTrue(manager.getGridWithIntervals().get(calculateStartInterval(task2)));
        assertFalse(manager.getGridWithIntervals().get(calculateStartInterval(task3)));
    }

    public long calculateStartInterval(Task task) {
        return (task.getStartTime().toEpochMilli()
                - manager.getProgramStartTime()) / MathConsts.MINUTE_IN_MILLIS
                / InMemoryTaskManager.MINUTES_INTERVAL;
    }
}
