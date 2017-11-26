package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;

public class NullGetter<T, P> implements Getter<T, P> {

    private static final NullGetter NULL_GETTER = new NullGetter();

    private NullGetter() {
    }

    @Override
    public String toString() {
        return "NullGetter{}";
    }

    @Override
    public P get(T target) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T, V> Getter<T, V> getter() {
        return NULL_GETTER;
    }

    public static boolean isNull(Getter<?, ?> getter) {
        return getter == null || getter == NULL_GETTER;
    }
}
