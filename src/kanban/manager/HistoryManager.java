package kanban.manager;

import kanban.models.Task;

import java.util.List;

/**
 * Интерфейс для управления историей просмотров
 */
public interface HistoryManager {

    /**
     * Пометить задачу как просмотренную
     *
     * @param task Задача
     */
    void add(Task task);

    /**
     * Удалить просмотренную задачу
     *
     * @param id Задачи
     */
    void remove(int id);

    /**
     * Получить список просмотренных задач
     *
     * @return Список просмотренных задач
     */
    List<Task> getHistory();
}
