package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public class BoxedLongContextualGetter<T> implements LongContextualGetter<T>, ContextualGetter<T, Long> {


    private final ContextualGetter<? super T, ? extends Long> delegate;

    public BoxedLongContextualGetter(ContextualGetter<? super T, ? extends Long> delegate) {
        this.delegate = delegate;
    }

    @Override
    public long getLong(T target, Context mappingContext) throws Exception {
        final Long value = get(target, mappingContext);
        if (value != null) {
            return value.longValue();
        }
        return 0;
    }

    @Override
    public Long get(T target, Context context) throws Exception {
        return delegate.get(target, context);
    }
}
