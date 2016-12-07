package org.simpleflatmapper.util;

public class ConstantUnaryFactory<T, P> implements UnaryFactory<T, P> {
    private final P value;

    private ConstantUnaryFactory(P value) {
        this.value = value;
    }

    @Override
    public P newInstance(T t) {
        return value;
    }

    public static <T, P> ConstantUnaryFactory<T, P> of(P value) {
        return new ConstantUnaryFactory<T, P>(value);
    }
}