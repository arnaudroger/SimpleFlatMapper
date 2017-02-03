package org.simpleflatmapper.reflect.setter;

import org.simpleflatmapper.reflect.Setter;

import java.util.List;

public class IndexedListSetter<E> implements Setter<List<E>, E> {
    private final int index;

    public IndexedListSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(List<E> target, E value) throws Exception {
        while (target.size() <= index) {
            target.add(null);
        }
        target.set(index, value);
    }
}
