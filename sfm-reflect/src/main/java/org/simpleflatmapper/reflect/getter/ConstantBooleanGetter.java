package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;

public class ConstantBooleanGetter<T> implements BooleanGetter<T>, Getter<T, Boolean> {
    private final boolean value;

    public ConstantBooleanGetter(boolean value) {
        this.value = value;
    }

    @Override
    public boolean getBoolean(T target) {
        return value;
    }

    @Override
    public Boolean get(T target) {
        return value;
    }
}
