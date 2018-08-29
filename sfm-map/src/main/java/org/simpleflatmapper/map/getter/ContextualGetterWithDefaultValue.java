package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;

public class ContextualGetterWithDefaultValue<T, P> implements ContextualGetter<T, P> {
    private final P defaultValue;
    private final ContextualGetter<? super T, ? extends P> delegate;

    public ContextualGetterWithDefaultValue(ContextualGetter<? super T, ? extends P> delegate, P defaultValue) {
        this.delegate = delegate;
        this.defaultValue = defaultValue;
    }

    @Override
    public P get(T t, Context context) throws Exception {
        P p = delegate.get(t, context);

        if (p == null) return defaultValue;

        return p;
    }
}
