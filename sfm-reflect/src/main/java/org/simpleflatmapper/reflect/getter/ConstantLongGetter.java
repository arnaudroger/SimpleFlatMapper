package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.LongGetter;

public class ConstantLongGetter<T> implements LongGetter<T>, Getter<T, Long> {
    private final long value;

    public ConstantLongGetter(long value) {
        this.value = value;
    }

    @Override
    public long getLong(T target) {
        return value;
    }

    @Override
    public Long get(T target) {
        return value;
    }
}
