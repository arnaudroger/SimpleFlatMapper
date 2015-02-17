package org.sfm.reflect.impl;

import org.sfm.reflect.Getter;

public class NullGetter<T, E> implements Getter<T, E> {

    @Override
    public E get(T target) throws Exception {
        return null;
    }

    @Override
    public String toString() {
        return "NullGetter{}";
    }
}
