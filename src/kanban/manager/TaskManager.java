package kanban.manager;

import kanban.models.Epic;
import kanban.models.Subtask;
import kanban.models.SubtasksStatus;
import kanban.models.Task;

import java.util.ArrayList;

public interface TaskManager {

    /**
     * Добавление нового {@link Epic}
     *
     * @param epic Объект для добавления
     * @return Присвоенный ID
     */
    int addEpic(Epic epic);


    /**
     * Обновление {@link Epic}
     *
     * @param epic Объект для обновления
     * @return Результат обновления
     */
    boolean updateEpic(Epic epic);

    /**
     * Удаление {@link Epic}
     *
     * @param epicId Id объекта
     * @return Результат удаления
     */
    boolean deleteEpicById(int epicId);

    /**
     * Полное удаление всех {@link Epic}
     */
    void clearEpics();
    /**
     * Получить {@link Epic} по ID
     *
     * @param epicId Id объекта
     * @return {@link Epic}, null - если не найден
     */
    Epic getEpicById(int epicId);
    /**
     * Удаление {@link Subtask} из {@link Epic}
     *
     * @param epicId Id объекта
     * @return Результат удаления
     */
    boolean clearSubtasksInEpic(int epicId);
    /**
     * Получить все {@link Subtask} из {@link Epic}
     *
     * @param epicId Id объекта
     * @return список
     */
    ArrayList<Subtask> getSubtasksByEpic(int epicId);
    /**
     * Добавление {@link Subtask} в {@link Epic}
     *
     * @param subtask объект для добавления
     * @return Присвоенный ID
     */
    int addSubtaskToEpic(Subtask subtask);
    /**
     * Обновление {@link Subtask}
     *
     * @param subtask Объект для обновления
     * @return Результат обновления
     */
    boolean updateSubtask(Subtask subtask);
    /**
     * Удаление {@link Subtask}
     *
     * @param subtaskId Id объекта
     * @return Результат удаления
     */
    boolean deleteSubtaskById(int subtaskId);
    /**
     * Полное удаление всех {@link Subtask}
     */
    void clearSubtasks();
    /**
     * Получить {@link Subtask} по ID
     *
     * @param subtaskId Id объекта
     * @return {@link Subtask}, null - если не найден
     */
    Subtask getSubtaskById(int subtaskId);
    /**
     * Добавление нового {@link Task}
     *
     * @param task Объект для добавления
     * @return Присвоенный ID
     */
    int addTask(Task task);
    /**
     * Обновление {@link Task}
     *
     * @param task Объект для обновления
     * @return Результат обновления
     */
    boolean updateTask(Task task);
    /**
     * Удаление {@link Task}
     *
     * @param taskId Id объекта
     * @return Результат удаления
     */
    boolean deleteTask(int taskId);
    /**
     * Полное удаление всех {@link Task}
     */
    void clearTasks();

    /**
     * Получить {@link Task} по ID
     * @param taskId
     * @return
     */
    Task getTaskById(int taskId);
}
