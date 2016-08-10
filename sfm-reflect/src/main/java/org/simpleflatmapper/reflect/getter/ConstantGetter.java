package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;

public class ConstantGetter<T, P> implements Getter<T, P> {
    private final P value;

    public ConstantGetter(P value) {
        this.value = value;
    }

    @Override
    public P get(T target) {
        return value;
    }
}
