package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;

public class NullGetter<P> implements Getter<Object, P> {

    private static final NullGetter NULL_GETTER = new NullGetter();

    private NullGetter() {
    }

    @Override
    public String toString() {
        return "NullGetter{}";
    }

    @Override
    public P get(Object target) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> NullGetter<T> getter() {
        return NULL_GETTER;
    }

    public static boolean isNull(Getter<?, ?> getter) {
        return getter == null || getter == NULL_GETTER;
    }
}
