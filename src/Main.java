import kanban.manager.FileBackedTasksManager;
import kanban.manager.Managers;
import kanban.manager.TaskManager;
import kanban.models.*;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        TaskManager inMemoryTaskManager = FileBackedTasksManager
                .loadFromFile(new File("resources/test_data.csv"));

        System.out.println(inMemoryTaskManager);

        Task task1 = new Task("task1");
        Task task2 = new Task("task2");

        System.out.println(task2.toCsvString());

        Epic epic1 = new Epic("epic1");
        Epic epic2 = new Epic("epic2", "description");

        Subtask sub1 = new Subtask("sub1");
        Subtask sub2 = new Subtask("sub2");
        Subtask sub3 = new Subtask("sub3");

        int task1Id = inMemoryTaskManager.addTask(task1);
        int task2Id = inMemoryTaskManager.addTask(task2);

        int epic1Id = inMemoryTaskManager.addEpic(epic1);
        int epic2Id = inMemoryTaskManager.addEpic(epic2);

        sub1.setEpicId(epic1Id);
        sub2.setEpicId(epic1Id);
        sub3.setEpicId(epic1Id);

        int sub1Id = inMemoryTaskManager.addSubtaskToEpic(sub1);
        int sub2Id = inMemoryTaskManager.addSubtaskToEpic(sub2);
        int sub3Id = inMemoryTaskManager.addSubtaskToEpic(sub3);

        inMemoryTaskManager.getTaskById(1);
        System.out.println(inMemoryTaskManager.getHistory());
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getEpicById(3);
        System.out.println(inMemoryTaskManager.getHistory());
        inMemoryTaskManager.getTaskById(1);
        System.out.println(inMemoryTaskManager.getHistory());
        inMemoryTaskManager.getSubtaskById(sub3Id);
        inMemoryTaskManager.getSubtaskById(sub1Id);
        inMemoryTaskManager.getSubtaskById(sub2Id);
        System.out.println(inMemoryTaskManager.getHistory());

        inMemoryTaskManager.deleteSubtaskById(sub2Id);
        System.out.println(inMemoryTaskManager.getHistory());

        //inMemoryTaskManager.clearEpics();
        System.out.println(inMemoryTaskManager.getHistory());
    }
}
