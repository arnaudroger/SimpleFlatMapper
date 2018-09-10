package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public class BoxedShortContextualGetter<T> implements ShortContextualGetter<T>, ContextualGetter<T, Short> {


    private final ContextualGetter<? super T, ? extends Short> delegate;

    public BoxedShortContextualGetter(ContextualGetter<? super T, ? extends Short> delegate) {
        this.delegate = delegate;
    }

    @Override
    public short getShort(T target, Context mappingContext) throws Exception {
        final Short value = get(target, mappingContext);
        if (value != null) {
            return value.shortValue();
        }
        return 0;
    }

    @Override
    public Short get(T target, Context context) throws Exception {
        return delegate.get(target, context);
    }
}
