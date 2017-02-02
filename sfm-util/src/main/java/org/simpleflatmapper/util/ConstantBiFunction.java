package org.simpleflatmapper.util;

public class ConstantBiFunction<P1, P2, T> implements BiFunction<P1, P2, T> {
    private final T t;

    public ConstantBiFunction(T t) {
        this.t = t;
    }

    @Override
    public T apply(P1 p1, P2 p2) {
        return t;
    }
}
