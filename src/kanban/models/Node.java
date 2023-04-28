package kanban.models;

/**
 * Узел для двусвязного списка
 *
 * @param <T> Элемент
 */
public class Node<T> {
    /**
     * Данные внутри узла
     */
    public T data;
    /**
     * Ссылка на следующий элемент
     */
    public Node<T> next;
    /**
     * Ссыдка на предыдущий элемент
     */
    public Node<T> prev;

    public Node(Node<T> prev, T data, Node<T> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}
