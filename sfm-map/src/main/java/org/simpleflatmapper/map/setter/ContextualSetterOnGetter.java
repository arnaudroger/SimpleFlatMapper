package org.simpleflatmapper.map.setter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.reflect.Getter;

public class ContextualSetterOnGetter<T, I, P> implements ContextualSetter<T, P> {
    private final Getter<P, I> getter;
    private final ContextualSetter<T, I> setter;

    public ContextualSetterOnGetter(ContextualSetter<T, I> setter, Getter<P, I> getter) {
        this.setter = setter;
        this.getter = getter;
    }

    @Override
    public void set(T target, P value, Context context) throws Exception {
        setter.set(target, getter.get(value), context);
    }
}
