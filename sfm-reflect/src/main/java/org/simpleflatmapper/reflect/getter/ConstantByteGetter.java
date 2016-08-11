package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;

public class ConstantByteGetter<T> implements ByteGetter<T>, Getter<T, Byte> {
    private final byte value;

    public ConstantByteGetter(byte value) {
        this.value = value;
    }

    @Override
    public byte getByte(T target) {
        return value;
    }

    @Override
    public Byte get(T target) {
        return value;
    }
}
