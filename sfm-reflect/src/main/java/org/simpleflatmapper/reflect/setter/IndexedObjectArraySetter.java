package org.simpleflatmapper.reflect.setter;

import org.simpleflatmapper.reflect.Setter;

public final class IndexedObjectArraySetter<E> implements Setter<E[], E> {
    private final int index;

    public IndexedObjectArraySetter(int index) {
        this.index = index;
    }

    @Override
    public void set(E[] target, E value) throws Exception {
        target[index] = value;
    }
}
