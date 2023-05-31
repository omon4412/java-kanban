package kanban.manager;

import kanban.models.TaskStatus;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    String path = "resources/test_data2.csv";

    @Override
    public FileBackedTasksManager createManager() {
        try {
            FileWriter writer = new FileWriter(path, false);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (FileBackedTasksManager) FileBackedTasksManager.loadFromFile(new File(path));
    }

    @Test
    public void saveAndRestoreData() {
        createEpicThreeSub(TaskStatus.NEW, TaskStatus.DONE, TaskStatus.DONE);
        manager.getEpicById(1);
        manager.getEpicById(4);

        TaskManager newMan = FileBackedTasksManager.loadFromFile(new File(path));
        assertEquals(manager.getEpics().get(0), newMan.getEpics().get(0));
        assertEquals(manager.getSubtasks().get(0), newMan.getSubtasks().get(0));
        assertEquals(manager.getSubtasks().get(1), newMan.getSubtasks().get(1));
        assertEquals(manager.getSubtasks().get(2), newMan.getSubtasks().get(2));
        assertEquals(manager.getHistory(), newMan.getHistory());
    }

    @Test
    public void saveAndRestoreDataWithEmptyHistory() {
        createEpicThreeSub(TaskStatus.NEW, TaskStatus.DONE, TaskStatus.DONE);

        TaskManager newMan = FileBackedTasksManager.loadFromFile(new File(path));
        assertEquals(manager.getEpics().get(0), newMan.getEpics().get(0));
        assertEquals(manager.getSubtasks().get(0), newMan.getSubtasks().get(0));
        assertEquals(manager.getSubtasks().get(1), newMan.getSubtasks().get(1));
        assertEquals(manager.getSubtasks().get(2), newMan.getSubtasks().get(2));
        assertEquals(manager.getHistory(), newMan.getHistory());
    }
}