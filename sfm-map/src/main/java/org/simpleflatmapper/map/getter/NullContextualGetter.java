package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public class NullContextualGetter<T, P> implements ContextualGetter<T, P> {

    private static final NullContextualGetter NULL_GETTER = new NullContextualGetter();

    private NullContextualGetter() {
    }

    @Override
    public String toString() {
        return "NullGetter{}";
    }

    @Override
    public P get(T target, Context context) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T, V> ContextualGetter<T, V> getter() {
        return NULL_GETTER;
    }

    public static boolean isNull(ContextualGetter<?, ?> getter) {
        return getter == null || getter == NULL_GETTER;
    }
}
