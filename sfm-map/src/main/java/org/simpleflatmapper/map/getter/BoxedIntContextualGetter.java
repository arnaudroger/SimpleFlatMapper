package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public class BoxedIntContextualGetter<T> implements IntContextualGetter<T>, ContextualGetter<T, Integer> {


    private final ContextualGetter<? super T, ? extends Integer> delegate;

    public BoxedIntContextualGetter(ContextualGetter<? super T, ? extends Integer> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int getInt(T target, Context mappingContext) throws Exception {
        final Integer value = get(target, mappingContext);
        if (value != null) {
            return value.intValue();
        }
        return 0;
    }

    @Override
    public Integer get(T target, Context context) throws Exception {
        return delegate.get(target, context);
    }
}
