package org.sfm.reflect;

public class IdentityGetter<T> implements Getter<T, T> {
    @Override
    public T get(T target) throws Exception {
        return target;
    }
}
