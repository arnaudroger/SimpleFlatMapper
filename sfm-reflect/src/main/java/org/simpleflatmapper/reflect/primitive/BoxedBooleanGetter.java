package org.simpleflatmapper.reflect.primitive;

import org.simpleflatmapper.reflect.Getter;

public class BoxedBooleanGetter<T> implements BooleanGetter<T>, Getter<T, Boolean> {


    private final Getter<? super T, ? extends Boolean> delegate;

    public BoxedBooleanGetter(Getter<? super T, ? extends Boolean> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean getBoolean(T target) throws Exception {
        final Boolean bool = get(target);
        if (bool != null) {
            return bool.booleanValue();
        }
        return false;
    }

    @Override
    public Boolean get(T target) throws Exception {
        return delegate.get(target);
    }
}
