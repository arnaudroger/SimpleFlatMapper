package org.sfm.reflect.impl;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.ByteGetter;

public class StaticByteGetter<T> implements ByteGetter, Getter<T, Byte> {
    private final byte value;

    public StaticByteGetter(byte value) {
        this.value = value;
    }

    @Override
    public byte getByte(Object target) throws Exception {
        return value;
    }

    @Override
    public Byte get(T target) throws Exception {
        return value;
    }
}
