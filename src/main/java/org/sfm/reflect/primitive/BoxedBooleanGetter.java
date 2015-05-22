package org.sfm.reflect.primitive;

import org.sfm.reflect.Getter;

public class BoxedBooleanGetter<T> implements BooleanGetter<T>, Getter<T, Boolean> {


    private final Getter<T, Boolean> delegate;

    public BoxedBooleanGetter(Getter<T, Boolean> delegate) {
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
