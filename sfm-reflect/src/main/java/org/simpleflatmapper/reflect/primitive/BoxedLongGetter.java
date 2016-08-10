package org.simpleflatmapper.reflect.primitive;

import org.simpleflatmapper.reflect.Getter;

public class BoxedLongGetter<T> implements LongGetter<T>, Getter<T, Long> {


    private final Getter<? super T, ? extends Long> delegate;

    public BoxedLongGetter(Getter<? super T, ? extends Long> delegate) {
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
