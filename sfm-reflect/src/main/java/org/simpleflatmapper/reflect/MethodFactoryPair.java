package org.simpleflatmapper.reflect;

import org.simpleflatmapper.util.BiFactory;

import java.lang.reflect.Method;

public class MethodFactoryPair<T1, T2> {
    private final Method method;
    private final BiFactory<? super T1, ? super T2, ?> factory;

    public MethodFactoryPair(Method method, BiFactory<? super T1, ? super T2, ?> factory) {
        this.method = method;
        this.factory = factory;
    }

    public Method getMethod() {
        return method;
    }

    public BiFactory<? super T1, ? super T2, ?> getFactory() {
        return factory;
    }
}
