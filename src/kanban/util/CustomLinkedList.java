package kanban.util;

import kanban.models.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Двусвязный список
 *
 * @param <T> Любой тип данных
 */
public class CustomLinkedList<T> {

    /**
     * "Голова" списка
     */
    private Node<T> head;
    /**
     * "Хвост" списка
     */
    private Node<T> tail;

    /**
     * Размер списка
     */
    private int size;

    /**
     * Добаление элемента в конец
     *
     * @param elem Элемент
     * @return Узел с новым элементом внутри списка
     */
    public Node<T> linkLast(T elem) {
        Node<T> newElem = new Node<>(tail, elem, null);
        Node<T> oldElem = tail;

        if (oldElem == null) {
            head = newElem;
        } else {
            oldElem.next = newElem;
        }
        tail = newElem;
        size++;
        return newElem;
    }

    /**
     * Удаоение узла
     *
     * @param elem узел
     */
    public void removeNode(Node<T> elem) {
        if (elem == null) return;

        if (elem.equals(head)) {
            if(size == 1){
                head = null;
                return;
            }
            head.next.prev = null;
            head = head.next;
        } else if (elem == tail) {
            tail.prev.next = null;
            tail = tail.prev;
        } else {
            elem.next.prev = elem.prev;
            elem.prev.next = elem.next;
        }
        elem.data = null;
        size--;
    }

    /**
     * Получение списка в виде ArrayList
     *
     * @return Список
     */
    public List<T> getTasks() {
        if (head == null) {
            return Collections.emptyList();
        }
        List<T> tasks = new ArrayList<>();
        Node<T> currElem = head;

        while (currElem != null) {
            tasks.add(currElem.data);
            currElem = currElem.next;
        }

        return tasks;
    }

    /**
     * Получить текущий размер списка
     *
     * @return Текущий размер списка
     */
    public int getSize() {
        return size;
    }
}
