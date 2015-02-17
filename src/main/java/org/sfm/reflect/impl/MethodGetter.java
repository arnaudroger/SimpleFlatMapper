package org.sfm.reflect.impl;

import org.sfm.reflect.Getter;

import java.lang.reflect.Method;

public class MethodGetter<T, P> implements Getter<T, P> {
    private final Method method;

    public MethodGetter(Method method) {
        this.method = method;
    }

    @Override
    public P get(T target) throws Exception {
        return (P) method.invoke(target);
    }
}
