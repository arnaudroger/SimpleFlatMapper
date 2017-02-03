package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;

public final class IndexedObjectArrayGetter<E> implements Getter<E[], E> {
    private final int index;

    public IndexedObjectArrayGetter(int index) {
        this.index = index;
    }

    @Override
    public E get(E[] target) throws Exception {
        return target[index];
    }
}
