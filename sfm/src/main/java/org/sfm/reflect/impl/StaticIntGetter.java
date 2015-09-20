package org.sfm.reflect.impl;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.IntGetter;

public class StaticIntGetter<T> implements IntGetter, Getter<T, Integer> {
    private final int value;

    public StaticIntGetter(int value) {
        this.value = value;
    }

    @Override
    public int getInt(Object target) throws Exception {
        return value;
    }

    @Override
    public Integer get(T target) throws Exception {
        return value;
    }
}
