package kanban.manager;

import kanban.models.Task;
import kanban.util.LimitedArrayList;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> historyTaskList;

    public InMemoryHistoryManager(int maxSize) {
        historyTaskList = new LimitedArrayList<>(maxSize);
    }

    @Override
    public void add(Task task) {
        historyTaskList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyTaskList;
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "size=" + historyTaskList.size() +
                ", historyTaskList=" + historyTaskList +
                '}';
    }
}
