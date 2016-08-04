package org.simpleflatmapper.core.reflect.getter;

import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.primitive.DoubleGetter;

public class ConstantDoubleGetter<T> implements DoubleGetter, Getter<T, Double> {
    private final double value;

    public ConstantDoubleGetter(double value) {
        this.value = value;
    }

    @Override
    public double getDouble(Object target) throws Exception {
        return value;
    }

    @Override
    public Double get(T target) throws Exception {
        return value;
    }
}
