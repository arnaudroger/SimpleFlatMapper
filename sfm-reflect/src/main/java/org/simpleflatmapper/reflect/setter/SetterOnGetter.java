package org.simpleflatmapper.reflect.setter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Setter;

public class SetterOnGetter<T, I, P> implements Setter<T, P> {
    private final Getter<P, I> getter;
    private final Setter<T, I> setter;

    public SetterOnGetter(Setter<T, I> setter, Getter<P, I> getter) {
        this.setter = setter;
        this.getter = getter;
    }

    @Override
    public void set(T target, P value) throws Exception {
        setter.set(target, getter.get(value));
    }
}
