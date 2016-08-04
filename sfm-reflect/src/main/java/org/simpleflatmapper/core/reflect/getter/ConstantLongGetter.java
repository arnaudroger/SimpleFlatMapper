package org.simpleflatmapper.core.reflect.getter;

import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.primitive.LongGetter;

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
