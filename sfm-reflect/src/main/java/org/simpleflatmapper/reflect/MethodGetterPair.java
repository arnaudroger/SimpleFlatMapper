package org.simpleflatmapper.reflect;

import java.lang.reflect.Method;

public class MethodGetterPair<T> {
    private final Method method;
    private final Getter<? super T, ?> getter;

    public MethodGetterPair(Method method, Getter<? super T, ?> getter) {
        this.method = method;
        this.getter = getter;
    }

    public Method getMethod() {
        return method;
    }

    public Getter<? super T, ?> getGetter() {
        return getter;
    }
}
