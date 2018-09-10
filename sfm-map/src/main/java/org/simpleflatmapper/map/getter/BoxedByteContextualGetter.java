package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public class BoxedByteContextualGetter<T> implements ByteContextualGetter<T>, ContextualGetter<T, Byte> {


    private final ContextualGetter<? super T, ? extends Byte> delegate;

    public BoxedByteContextualGetter(ContextualGetter<? super T, ? extends Byte> delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte getByte(T target, Context mappingContext) throws Exception {
        final Byte value = get(target, mappingContext);
        if (value != null) {
            return value.byteValue();
        }
        return 0;
    }

    @Override
    public Byte get(T target, Context context) throws Exception {
        return delegate.get(target, context);
    }
}
