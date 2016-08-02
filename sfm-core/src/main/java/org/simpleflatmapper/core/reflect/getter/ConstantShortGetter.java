package org.simpleflatmapper.core.reflect.getter;

import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.primitive.ShortGetter;

public class ConstantShortGetter<T> implements ShortGetter, Getter<T, Short> {
    private final short value;

    public ConstantShortGetter(short value) {
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
