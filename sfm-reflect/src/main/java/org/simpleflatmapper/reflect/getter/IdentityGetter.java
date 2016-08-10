package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;

public class IdentityGetter<T> implements Getter<T, T> {
    @Override
    public T get(T target) {
        return target;
    }
}
