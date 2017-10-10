package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.reflect.Setter;

public class NullValueFilterSetter<T, P> implements Setter<T, P> {
    private final Setter<? super T, ? super P> delegate;

    public NullValueFilterSetter(Setter<? super T, ? super P> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void set(T target, P value) throws Exception {
        if (value != null) {
            delegate.set(target, value);
        }
    }
}
