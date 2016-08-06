package org.simpleflatmapper.reflect;


public class NullSetter<T, P> implements Setter<T, P> {

    public static final NullSetter NULL_SETTER = new NullSetter();

    private NullSetter() {
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

    public static boolean isNull(Setter<?,?> setter) {
        return setter == null || setter == NULL_SETTER;
    }
}
