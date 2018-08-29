package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public class BoxedDoubleContextualGetter<T> implements DoubleContextualGetter<T>, ContextualGetter<T, Double> {


    private final ContextualGetter<? super T, ? extends Double> delegate;

    public BoxedDoubleContextualGetter(ContextualGetter<? super T, ? extends Double> delegate) {
        this.delegate = delegate;
    }

    @Override
    public double getDouble(T target, Context mappingContext) throws Exception {
        final Double value = get(target, mappingContext);
        if (value != null) {
            return value.doubleValue();
        }
        return 0;
    }

    @Override
    public Double get(T target, Context context) throws Exception {
        return delegate.get(target, context);
    }
}
