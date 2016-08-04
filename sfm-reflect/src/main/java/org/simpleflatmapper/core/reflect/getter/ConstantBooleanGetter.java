package org.simpleflatmapper.core.reflect.getter;

import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.primitive.BooleanGetter;

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
