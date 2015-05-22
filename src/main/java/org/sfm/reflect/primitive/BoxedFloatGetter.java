package org.sfm.reflect.primitive;

import org.sfm.reflect.Getter;

public class BoxedFloatGetter<T> implements FloatGetter<T>, Getter<T, Float> {


    private final Getter<T, Float> delegate;

    public BoxedFloatGetter(Getter<T, Float> delegate) {
        this.delegate = delegate;
    }

    @Override
    public float getFloat(T target) throws Exception {
        final Float value = get(target);
        if (value != null) {
            return value.floatValue();
        }
        return 0;
    }

    @Override
    public Float get(T target) throws Exception {
        return delegate.get(target);
    }
}
