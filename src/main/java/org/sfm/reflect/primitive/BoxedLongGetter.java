package org.sfm.reflect.primitive;

import org.sfm.reflect.Getter;

public class BoxedLongGetter<T> implements LongGetter<T>, Getter<T, Long> {


    private final Getter<T, Long> delegate;

    public BoxedLongGetter(Getter<T, Long> delegate) {
        this.delegate = delegate;
    }

    @Override
    public long getLong(T target) throws Exception {
        final Long value = get(target);
        if (value != null) {
            return value.longValue();
        }
        return 0;
    }

    @Override
    public Long get(T target) throws Exception {
        return delegate.get(target);
    }
}
