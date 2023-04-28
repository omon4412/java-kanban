package kanban.manager;

import kanban.models.Task;
import kanban.models.Node;
import kanban.util.CustomLinkedList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> historyTaskList;
    private final Map<Integer, Node<Task>> historyTaskMap;

    public InMemoryHistoryManager() {
        historyTaskList = new CustomLinkedList<>();
        historyTaskMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (historyTaskMap.containsKey(task.getId())) {
            historyTaskList.removeNode(historyTaskMap.get(task.getId()));
        }
        Node<Task> newTask = historyTaskList.linkLast(task);
        historyTaskMap.put(task.getId(), newTask);
    }

    @Override
    public void remove(int id) {
        historyTaskList.removeNode(historyTaskMap.get(id));
        historyTaskMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyTaskList.getTasks();
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "size=" + historyTaskList.getSize() +
                ", historyTaskList=" + historyTaskList +
                '}';
    }
}
