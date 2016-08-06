package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;

import java.lang.reflect.Method;

public class MethodGetter<T, P> implements Getter<T, P> {
    private final Method method;

    public MethodGetter(Method method) {
        this.method = method;
    }

    @SuppressWarnings("unchecked")
    @Override
    public P get(T target) throws Exception {
        return (P) method.invoke(target);
    }

    @Override
    public String toString() {
        return "MethodGetter{" +
                "method=" + method +
                '}';
    }
}
