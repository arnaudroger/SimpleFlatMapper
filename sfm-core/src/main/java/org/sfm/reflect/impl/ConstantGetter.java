package org.sfm.reflect.impl;

import org.sfm.reflect.Getter;

public class ConstantGetter<T, P> implements Getter<T, P> {
    private final P value;

    public ConstantGetter(P value) {
        this.value = value;
    }

    @Override
    public P get(T target) throws Exception {
        return value;
    }
}
