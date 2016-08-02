package org.simpleflatmapper.core.reflect.primitive;

import org.simpleflatmapper.core.reflect.Getter;

public class BoxedLongGetter<T> implements LongGetter<T>, Getter<T, Long> {


    private final Getter<? super T, Long> delegate;

    public BoxedLongGetter(Getter<? super T, Long> delegate) {
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
