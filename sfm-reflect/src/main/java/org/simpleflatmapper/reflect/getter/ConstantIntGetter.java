package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.IntGetter;

public class ConstantIntGetter<T> implements IntGetter<T>, Getter<T, Integer> {
    private final int value;

    public ConstantIntGetter(int value) {
        this.value = value;
    }

    @Override
    public int getInt(T target) {
        return value;
    }

    @Override
    public Integer get(T target) {
        return value;
    }
}
