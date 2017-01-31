package org.simpleflatmapper.util;

public class ConstantBiFactory<P1, P2, T> implements BiFactory<P1, P2, T> {
    private final T t;

    public ConstantBiFactory(T t) {
        this.t = t;
    }

    @Override
    public T newInstance(P1 p1, P2 p2) {
        return t;
    }
}
