package org.simpleflatmapper.reflect.primitive;

import org.simpleflatmapper.reflect.Getter;

public class BoxedShortGetter<T> implements ShortGetter<T>, Getter<T, Short> {


    private final Getter<? super T, ? extends Short> delegate;

    public BoxedShortGetter(Getter<? super T, ? extends Short> delegate) {
        this.delegate = delegate;
    }

    @Override
    public short getShort(T target) throws Exception {
        final Short value = get(target);
        if (value != null) {
            return value.shortValue();
        }
        return 0;
    }

    @Override
    public Short get(T target) throws Exception {
        return delegate.get(target);
    }
}
