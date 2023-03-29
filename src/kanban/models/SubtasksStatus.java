package kanban.models;

public enum SubtasksStatus {

    SUBTASKS_DONE, // Все подзадачи выполнены
    NONE_SUBTASKS_DONE_OR_IN_PROGRESS, // Хотя бы одна задача выполнена или находится в процессе выполнения, но не все
    ONE_IN_PROGRESS, // Хотя бы одна задача в процессе выполнения
}
