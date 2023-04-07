import kanban.manager.InMemoryTaskManager;
import kanban.manager.Managers;
import kanban.models.Epic;
import kanban.models.Subtask;
import kanban.models.Task;
import kanban.models.TaskStatus;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefaultManager();

        Task task1 = new Task("task1");
        Task task2 = new Task("task2");

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
        sub3.setEpicId(epic2Id);

        int sub1Id = inMemoryTaskManager.addSubtaskToEpic(sub1);
        int sub2Id = inMemoryTaskManager.addSubtaskToEpic(sub2);
        int sub3Id = inMemoryTaskManager.addSubtaskToEpic(sub3);

        inMemoryTaskManager.getTaskById(1);
        System.out.println(inMemoryTaskManager.getInMemoryHistoryManager());
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getEpicById(3);
        System.out.println(inMemoryTaskManager.getInMemoryHistoryManager());
        inMemoryTaskManager.getTaskById(1);
        System.out.println(inMemoryTaskManager.getInMemoryHistoryManager());

        System.out.println(inMemoryTaskManager.getInMemoryHistoryManager().getHistory());

        System.out.println(inMemoryTaskManager);
        System.out.println(task1);
        System.out.println(task2);
        System.out.println(epic1);
        System.out.println(epic2);
        System.out.println(sub1);
        System.out.println(sub2);
        System.out.println(sub3);

        Subtask sub1Copy = inMemoryTaskManager.getSubtaskById(sub1Id);
        sub1Copy.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(sub1Copy);
        Subtask sub2Copy = inMemoryTaskManager.getSubtaskById(sub2Id);
        sub2Copy.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(sub2Copy);

        printData(inMemoryTaskManager, epic1, epic2, sub1Id, sub2Id);

        inMemoryTaskManager.deleteTask(task1Id);
        inMemoryTaskManager.deleteEpicById(epic2Id);
        inMemoryTaskManager.deleteSubtaskById(sub1Id);

        printData(inMemoryTaskManager, epic1, epic2, sub1Id, sub2Id);

        inMemoryTaskManager.clearSubtasks();
        printData(inMemoryTaskManager, epic1, epic2, sub1Id, sub2Id);

        inMemoryTaskManager.clearEpics();
        printData(inMemoryTaskManager, inMemoryTaskManager.getEpicById(epic1.getId()), inMemoryTaskManager.getEpicById(epic2.getId()), sub1Id, sub2Id);
    }

    private static void printData(InMemoryTaskManager inMemoryTaskManager, Epic epic1, Epic epic2, int sub1Id, int sub2Id) {
        System.out.println();
        System.out.println(inMemoryTaskManager);
        System.out.println(epic1);
        System.out.println(epic2);
        System.out.println(inMemoryTaskManager.getSubtaskById(sub1Id));
        System.out.println(inMemoryTaskManager.getSubtaskById(sub2Id));
    }
}
