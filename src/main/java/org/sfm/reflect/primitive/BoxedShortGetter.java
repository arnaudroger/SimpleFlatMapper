package org.sfm.reflect.primitive;

import org.sfm.reflect.Getter;

public class BoxedShortGetter<T> implements ShortGetter<T>, Getter<T, Short> {


    private final Getter<T, Short> delegate;

    public BoxedShortGetter(Getter<T, Short> delegate) {
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
