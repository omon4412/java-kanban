import kanban.manager.TaskManager;
import kanban.models.Epic;
import kanban.models.Subtask;
import kanban.models.Task;
import kanban.models.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("task1");
        Task task2 = new Task("task2");

        Epic epic1 = new Epic("epic1");
        Epic epic2 = new Epic("epic2", "description");

        Subtask sub1 = new Subtask("sub1");
        Subtask sub2 = new Subtask("sub2");
        Subtask sub3 = new Subtask("sub3");

        int task1Id = taskManager.addTask(task1);
        int task2Id = taskManager.addTask(task2);

        int epic1Id = taskManager.addEpic(epic1);
        int epic2Id = taskManager.addEpic(epic2);

        sub1.setEpicId(epic1Id);
        sub2.setEpicId(epic1Id);
        sub3.setEpicId(epic2Id);

        int sub1Id = taskManager.addSubtaskToEpic(sub1);
        int sub2Id = taskManager.addSubtaskToEpic(sub2);
        int sub3Id = taskManager.addSubtaskToEpic(sub3);

        System.out.println(taskManager);
        System.out.println(task1);
        System.out.println(task2);
        System.out.println(epic1);
        System.out.println(epic2);
        System.out.println(sub1);
        System.out.println(sub2);
        System.out.println(sub3);

        Subtask sub1Copy = taskManager.getSubtaskById(sub1Id);
        sub1Copy.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(sub1Copy);
        Subtask sub2Copy = taskManager.getSubtaskById(sub2Id);
        sub2Copy.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(sub2Copy);

        printData(taskManager, epic1, epic2, sub1Id, sub2Id);

        taskManager.deleteTask(task1Id);
        taskManager.deleteEpicById(epic2Id);
        taskManager.deleteSubtaskById(sub1Id);

        printData(taskManager, epic1, epic2, sub1Id, sub2Id);

        taskManager.clearSubtasks();
        printData(taskManager, epic1, epic2, sub1Id, sub2Id);

        taskManager.clearEpics();
        printData(taskManager, taskManager.getEpicById(epic1.getId()), taskManager.getEpicById(epic2.getId()), sub1Id, sub2Id);
    }

    private static void printData(TaskManager taskManager, Epic epic1, Epic epic2, int sub1Id, int sub2Id) {
        System.out.println();
        System.out.println(taskManager);
        System.out.println(epic1);
        System.out.println(epic2);
        System.out.println(taskManager.getSubtaskById(sub1Id));
        System.out.println(taskManager.getSubtaskById(sub2Id));
    }
}
