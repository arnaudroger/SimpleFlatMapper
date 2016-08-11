package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;

public class ConstantDoubleGetter<T> implements DoubleGetter<T>, Getter<T, Double> {
    private final double value;

    public ConstantDoubleGetter(double value) {
        this.value = value;
    }

    @Override
    public double getDouble(T target) {
        return value;
    }

    @Override
    public Double get(T target) {
        return value;
    }
}
