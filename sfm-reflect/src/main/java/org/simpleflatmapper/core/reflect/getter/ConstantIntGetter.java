package org.simpleflatmapper.core.reflect.getter;

import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.primitive.IntGetter;

public class ConstantIntGetter<T> implements IntGetter, Getter<T, Integer> {
    private final int value;

    public ConstantIntGetter(int value) {
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
