package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public class BoxedBooleanContextualGetter<T> implements BooleanContextualGetter<T>, ContextualGetter<T, Boolean> {


    private final ContextualGetter<? super T, ? extends Boolean> delegate;

    public BoxedBooleanContextualGetter(ContextualGetter<? super T, ? extends Boolean> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean getBoolean(T target, Context mappingContext) throws Exception {
        final Boolean bool = get(target, mappingContext);
        if (bool != null) {
            return bool.booleanValue();
        }
        return false;
    }

    @Override
    public Boolean get(T target, Context context) throws Exception {
        return delegate.get(target, context);
    }
}
