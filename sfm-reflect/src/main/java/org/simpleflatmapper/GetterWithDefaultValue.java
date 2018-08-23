package org.simpleflatmapper;

import org.simpleflatmapper.reflect.Getter;

public class GetterWithDefaultValue<T, P> implements Getter<T, P> {
    private final Getter<? super T, ? extends P> delegate;
    private final P defaultValue;

    public GetterWithDefaultValue(Getter<? super T, ? extends P> delegate, P defaultValue) {
        this.delegate = delegate;
        this.defaultValue = defaultValue;
    }

    @Override
    public P get(T target) throws Exception {
        P p = delegate.get(target);
        
        if (p == null) return defaultValue;
        
        return p;
    }
}
