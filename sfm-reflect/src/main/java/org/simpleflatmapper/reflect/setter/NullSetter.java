package org.simpleflatmapper.reflect.setter;


import org.simpleflatmapper.reflect.Setter;

public class NullSetter implements Setter<Object, Object> {

    public static final NullSetter NULL_SETTER = new NullSetter();

    private NullSetter() {
    }

    @Override
    public void set(Object target, Object value) {
    }
    @Override
    public String toString() {
        return "NullSetter{}";
    }

    @SuppressWarnings("unchecked")

    public static boolean isNull(Setter<?,?> setter) {
        return setter == null || setter == NULL_SETTER;
    }
}
