package org.simpleflatmapper.reflect.primitive;

import org.simpleflatmapper.reflect.Getter;

public class BoxedIntGetter<T> implements IntGetter<T>, Getter<T, Integer> {


    private final Getter<? super T, ? extends Integer> delegate;

    public BoxedIntGetter(Getter<? super T, ? extends Integer> delegate) {
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
