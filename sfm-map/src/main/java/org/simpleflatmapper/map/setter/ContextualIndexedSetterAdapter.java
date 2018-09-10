package org.simpleflatmapper.map.setter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.reflect.IndexedSetter;

public class ContextualIndexedSetterAdapter<T, P> implements ContextualIndexedSetter<T, P> {
    
    private final IndexedSetter<T, P> setter;

    public ContextualIndexedSetterAdapter(IndexedSetter<T, P> setter) {
        this.setter = setter;
    }

    public static <T, P> ContextualIndexedSetter<T, P> of(IndexedSetter<T, P> indexedSetter) {
        if (indexedSetter == null) return null;
        return new ContextualIndexedSetterAdapter<T, P>(indexedSetter);
    }

    @Override
    public void set(T target, P value, int index, Context context) throws Exception {
        setter.set(target, value, index);
    }
}
