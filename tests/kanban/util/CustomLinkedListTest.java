package kanban.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CustomLinkedListTest {

    CustomLinkedList<Integer> list;

    @BeforeEach
    void recreateList() {
        list = new CustomLinkedList<>();
    }

    @Test
    void add8_10and15() {
        list.linkLast(8);
        list.linkLast(10);
        list.linkLast(15);
        assertEquals(3, list.getSize());
        assertEquals(new ArrayList<Integer>() {{
            add(8);
            add(10);
            add(15);
        }}, list.getTasks());
    }

    @Test
    void removeNodeFromBegin() {
        var forRemove = list.linkLast(8);
        list.linkLast(10);
        list.linkLast(15);
        list.removeNode(forRemove);
        assertEquals(2, list.getSize());
        assertEquals(new ArrayList<Integer>() {{
            add(10);
            add(15);
        }}, list.getTasks());
    }

    @Test
    void removeNodeFromMiddle() {
        list.linkLast(8);
        var forRemove = list.linkLast(10);
        list.linkLast(15);
        list.removeNode(forRemove);
        assertEquals(2, list.getSize());
        assertEquals(new ArrayList<Integer>() {{
            add(8);
            add(15);
        }}, list.getTasks());
    }

    @Test
    void removeNodeFromEnd() {
        list.linkLast(8);
        list.linkLast(10);
        var forRemove = list.linkLast(15);
        list.removeNode(forRemove);
        assertEquals(2, list.getSize());
        assertEquals(new ArrayList<Integer>() {{
            add(8);
            add(10);
        }}, list.getTasks());
    }

    @Test
    void getSizeTest() {
        assertEquals(0, list.getSize());
        list.linkLast(8);
        assertEquals(1, list.getSize());
        var forRemove = list.linkLast(10);
        assertEquals(2, list.getSize());
        list.linkLast(15);
        assertEquals(3, list.getSize());
        list.removeNode(forRemove);
        assertEquals(2, list.getSize());
    }
}