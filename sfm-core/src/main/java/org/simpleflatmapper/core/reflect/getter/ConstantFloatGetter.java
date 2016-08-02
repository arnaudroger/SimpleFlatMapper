package org.simpleflatmapper.core.reflect.getter;

import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.primitive.FloatGetter;

public class ConstantFloatGetter<T> implements FloatGetter, Getter<T, Float> {
    private final float value;

    public ConstantFloatGetter(float value) {
        this.value = value;
    }

    @Override
    public float getFloat(Object target) throws Exception {
        return value;
    }

    @Override
    public Float get(T target) throws Exception {
        return value;
    }
}
