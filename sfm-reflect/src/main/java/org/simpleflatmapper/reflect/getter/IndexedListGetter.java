package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;

import java.util.List;

public class IndexedListGetter<E> implements Getter<List<E>, E> {
    private final int index;

    public IndexedListGetter(int index) {
        this.index = index;
    }

    @Override
    public E get(List<E> target) throws Exception {
        if (index < target.size()) {
            return target.get(index);
        }
        return null;
    }
}
