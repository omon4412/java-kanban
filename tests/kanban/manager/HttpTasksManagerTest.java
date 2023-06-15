package kanban.manager;

import kanban.models.Epic;
import kanban.models.Subtask;
import kanban.models.Task;
import kanban.models.TaskStatus;
import kanban.server.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTasksManagerTest extends TaskManagerTest<HttpTaskManager> {

    protected KVServer server = new KVServer() {{
        start();
    }};

    public Task task2;
    public Epic epic2;
    public Subtask subtask2;

    @BeforeEach
    public void createTaskEpicSubtask() {
        task = new Task("Task name", "Task desc", TaskStatus.NEW);
        epic = new Epic("Epic name", "Epic desc");
        subtask = new Subtask("Sub name", "Sub desc", TaskStatus.NEW);
        task2 = new Task("Task name2", "Task desc2", TaskStatus.NEW);
        epic2 = new Epic("Epic name2", "Epic desc2");
        subtask2 = new Subtask("Sub name2", "Sub desc2", TaskStatus.NEW);
    }

    public HttpTasksManagerTest() throws IOException {
    }

    @Override
    public HttpTaskManager createManager() {
        return Managers.getHttpTaskManager();
    }

    @AfterEach
    void serverStop() throws IOException {
        server.stop();

    }

    @Test
    public void saveAndRestoreData() {
        manager.addTask(task);
        manager.addTask(task2);
        int epicId1 = manager.addEpic(epic);
        int epicId2 = manager.addEpic(epic2);
        subtask.setEpicId(epicId1);
        subtask2.setEpicId(epicId2);
        manager.addSubtaskToEpic(subtask);
        manager.addSubtaskToEpic(subtask2);
        manager.getEpicById(3);
        manager.getTaskById(1);
        manager.save();

        HttpTaskManager newManager = Managers.getHttpTaskManager();
        newManager.load();
        assertEquals(manager.getTasks().get(0), newManager.getTasks().get(0));
        assertEquals(manager.getTasks().get(1), newManager.getTasks().get(1));
        assertEquals(manager.getEpics().size(), newManager.getEpics().size());
        assertEquals(manager.getSubtasks().get(0), newManager.getSubtasks().get(0));
        assertEquals(manager.getSubtasks().get(1), newManager.getSubtasks().get(1));
    }
}