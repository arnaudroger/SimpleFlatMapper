package org.sfm.tuples;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Tuple4<T1, T2, T3, T4> {

    private final T1 element1;
    private final T2 element2;
    private final T3 element3;
    private final T4 element4;

    public Tuple4(T1 element1, T2 element2, T3 element3, T4 element4) {
        this.element1 = element1;
        this.element2 = element2;
        this.element3 = element3;
        this.element4 = element4;
    }

    public T1 getElement1() {
        return element1;
    }

    public T2 getElement2() {
        return element2;
    }

    public T3 getElement3() {
        return element3;
    }

    public T4 getElement4() {
        return element4;
    }
}
