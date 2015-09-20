package org.sfm.reflect.impl;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.BooleanGetter;

public class StaticGetter<T, P> implements Getter<T, P> {
    private final P value;

    public StaticGetter(P value) {
        this.value = value;
    }

    @Override
    public P get(T target) throws Exception {
        return value;
    }
}
