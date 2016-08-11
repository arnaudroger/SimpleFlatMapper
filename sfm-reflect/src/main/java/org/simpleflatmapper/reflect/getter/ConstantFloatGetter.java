package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;

public class ConstantFloatGetter<T> implements FloatGetter<T>, Getter<T, Float> {
    private final float value;

    public ConstantFloatGetter(float value) {
        this.value = value;
    }

    @Override
    public float getFloat(T target) {
        return value;
    }

    @Override
    public Float get(T target) {
        return value;
    }
}
