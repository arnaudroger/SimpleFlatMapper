package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public class NullFieldMapperGetter<T, P> implements FieldMapperGetter<T, P> {

    private static final NullFieldMapperGetter NULL_GETTER = new NullFieldMapperGetter();

    private NullFieldMapperGetter() {
    }

    @Override
    public String toString() {
        return "NullGetter{}";
    }

    @Override
    public P get(T target, MappingContext<?> context) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T, V> FieldMapperGetter<T, V> getter() {
        return NULL_GETTER;
    }

    public static boolean isNull(FieldMapperGetter<?, ?> getter) {
        return getter == null || getter == NULL_GETTER;
    }
}
