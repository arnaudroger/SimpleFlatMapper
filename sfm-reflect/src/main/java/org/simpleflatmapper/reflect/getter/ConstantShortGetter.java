package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;

public class ConstantShortGetter<T> implements ShortGetter<T>, Getter<T, Short> {
    private final short value;

    public ConstantShortGetter(short value) {
        this.value = value;
    }

    @Override
    public short getShort(T target) {
        return value;
    }

    @Override
    public Short get(T target) {
        return value;
    }
}
