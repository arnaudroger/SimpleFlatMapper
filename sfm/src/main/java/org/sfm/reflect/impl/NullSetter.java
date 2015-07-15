package org.sfm.reflect.impl;


import org.sfm.reflect.Setter;

public class NullSetter<T, P> implements Setter<T, P> {

    public static final NullSetter NULL_SETTER = new NullSetter();

    public static boolean isNull(Object setter) {
        return setter == null || setter instanceof NullSetter;
    }

    @Override
    public void set(T target, P value) throws Exception {
    }
    @Override
    public String toString() {
        return "NullSetter{}";
    }

    @SuppressWarnings("unchecked")
    public static <T, E> Setter<T, E> setter() {
        return NULL_SETTER;
    }
}
