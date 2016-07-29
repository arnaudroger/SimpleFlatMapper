package org.sfm.reflect.impl;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.LongGetter;

public class ConstantLongGetter<T> implements LongGetter, Getter<T, Long> {
    private final long value;

    public ConstantLongGetter(long value) {
        this.value = value;
    }

    @Override
    public long getLong(Object target) throws Exception {
        return value;
    }

    @Override
    public Long get(T target) throws Exception {
        return value;
    }
}
