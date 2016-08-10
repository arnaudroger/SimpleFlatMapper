package org.simpleflatmapper.reflect.primitive;

import org.simpleflatmapper.reflect.Getter;

public class BoxedDoubleGetter<T> implements DoubleGetter<T>, Getter<T, Double> {


    private final Getter<? super T, ? extends Double> delegate;

    public BoxedDoubleGetter(Getter<? super T, ? extends Double> delegate) {
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
