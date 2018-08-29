package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public class BoxedFloatContextualGetter<T> implements FloatContextualGetter<T>, ContextualGetter<T, Float> {


    private final ContextualGetter<? super T, ? extends Float> delegate;

    public BoxedFloatContextualGetter(ContextualGetter<? super T, ? extends Float> delegate) {
        this.delegate = delegate;
    }

    @Override
    public float getFloat(T target, Context mappingContext) throws Exception {
        final Float value = get(target, mappingContext);
        if (value != null) {
            return value.floatValue();
        }
        return 0;
    }

    @Override
    public Float get(T target, Context context) throws Exception {
        return delegate.get(target, context);
    }
}
