package org.sfm.reflect.impl;

import org.sfm.reflect.Getter;

public class NullGetter<T, P> implements Getter<T, P> {

    public static final NullGetter NULL_GETTER = new NullGetter();

    private NullGetter() {
    }

    @Override
    public String toString() {
        return "NullGetter{}";
    }

    @Override
    public P get(T target) throws Exception {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T, E> Getter<T, E> getter() {
        return NULL_GETTER;
    }

    public static boolean isNull(Getter<?, ?> getter) {
        return getter == null || getter == NULL_GETTER;
    }
}
