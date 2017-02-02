package org.simpleflatmapper.reflect;

import org.simpleflatmapper.util.BiFunction;

import java.lang.reflect.Method;

public class MethodBiFunctionPair<T1, T2> {
    private final Method method;
    private final BiFunction<? super T1, ? super T2, ?> function;

    public MethodBiFunctionPair(Method method, BiFunction<? super T1, ? super T2, ?> function) {
        this.method = method;
        this.function = function;
    }

    public Method getMethod() {
        return method;
    }

    public BiFunction<? super T1, ? super T2, ?> getFunction() {
        return function;
    }
}
