package org.simpleflatmapper.core.reflect.primitive;

import org.simpleflatmapper.core.reflect.Getter;

public class BoxedByteGetter<T> implements ByteGetter<T>, Getter<T, Byte> {


    private final Getter<? super T, Byte> delegate;

    public BoxedByteGetter(Getter<? super T, Byte> delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte getByte(T target) throws Exception {
        final Byte value = get(target);
        if (value != null) {
            return value.byteValue();
        }
        return 0;
    }

    @Override
    public Byte get(T target) throws Exception {
        return delegate.get(target);
    }
}
