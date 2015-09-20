package org.sfm.reflect.impl;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.ShortGetter;

public class StaticShortGetter<T> implements ShortGetter, Getter<T, Short> {
    private final short value;

    public StaticShortGetter(short value) {
        this.value = value;
    }

    @Override
    public short getShort(Object target) throws Exception {
        return value;
    }

    @Override
    public Short get(T target) throws Exception {
        return value;
    }
}
