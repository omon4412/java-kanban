package kanban.manager;

import kanban.models.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);
    List<Task> getHistory();
}
