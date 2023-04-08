package kanban.manager;

import kanban.models.Task;

import java.util.List;

/**
 * Интерфейс для управления историей просмотров
 */
public interface HistoryManager {

    /**
     * Пометить задачу как просотренную
     *
     * @param task Задача
     */
    void add(Task task);

    /**
     * Получить список просмотренных задач
     *
     * @return Список просмотренных задач
     */
    List<Task> getHistory();
}
