package org.sfm.utils;

public class ConstantUnaryFactory<T, P> implements UnaryFactory<T, P>{
    private final P value;

    public ConstantUnaryFactory(P value) {
        this.value = value;
    }

    @Override
    public P newInstance(T t) {
        return value;
    }
}
