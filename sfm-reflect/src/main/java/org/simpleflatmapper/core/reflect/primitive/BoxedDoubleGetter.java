package org.simpleflatmapper.core.reflect.primitive;

import org.simpleflatmapper.core.reflect.Getter;

public class BoxedDoubleGetter<T> implements DoubleGetter<T>, Getter<T, Double> {


    private final Getter<? super T, Double> delegate;

    public BoxedDoubleGetter(Getter<? super T, Double> delegate) {
        this.delegate = delegate;
    }

    @Override
    public double getDouble(T target) throws Exception {
        final Double value = get(target);
        if (value != null) {
            return value.doubleValue();
        }
        return 0;
    }

    @Override
    public Double get(T target) throws Exception {
        return delegate.get(target);
    }
}
