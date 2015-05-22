package org.sfm.reflect.primitive;

import org.sfm.reflect.Getter;

public class BoxedIntGetter<T> implements IntGetter<T>, Getter<T, Integer> {


    private final Getter<T, Integer> delegate;

    public BoxedIntGetter(Getter<T, Integer> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int getInt(T target) throws Exception {
        final Integer value = get(target);
        if (value != null) {
            return value.intValue();
        }
        return 0;
    }

    @Override
    public Integer get(T target) throws Exception {
        return delegate.get(target);
    }
}
