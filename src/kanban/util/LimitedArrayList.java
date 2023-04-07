package kanban.util;

import java.util.ArrayList;

/**
 * Список с ограничением в количестве элементов
 * @param <T> Тип элемента в этом списке
 */
public class LimitedArrayList<T> extends ArrayList<T> {

    private int maxSize;

    public LimitedArrayList(int maxSize) {
        super();
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T t) {
        boolean added = super.add(t);

        if (this.size() > maxSize) {
            this.remove(0);
        }
        return added;
    }
}
