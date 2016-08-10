package org.simpleflatmapper.reflect.primitive;

import org.simpleflatmapper.reflect.Getter;

public class BoxedByteGetter<T> implements ByteGetter<T>, Getter<T, Byte> {


    private final Getter<? super T, ? extends Byte> delegate;

    public BoxedByteGetter(Getter<? super T, ? extends Byte> delegate) {
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
