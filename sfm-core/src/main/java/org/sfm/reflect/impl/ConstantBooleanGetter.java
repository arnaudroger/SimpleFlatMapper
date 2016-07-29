package org.sfm.reflect.impl;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.BooleanGetter;

public class ConstantBooleanGetter<T> implements BooleanGetter, Getter<T, Boolean> {
    private final boolean value;

    public ConstantBooleanGetter(boolean value) {
        this.value = value;
    }

    @Override
    public boolean getBoolean(Object target) throws Exception {
        return value;
    }

    @Override
    public Boolean get(T target) throws Exception {
        return value;
    }
}
