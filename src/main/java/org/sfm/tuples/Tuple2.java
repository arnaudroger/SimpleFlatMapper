package org.sfm.tuples;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Tuple2<T1, T2> {

    private final T1 element1;
    private final T2 element2;

    public Tuple2(T1 element1, T2 element2) {
        this.element1 = element1;
        this.element2 = element2;
    }

    public T1 getElement1() {
        return element1;
    }

    public T2 getElement2() {
        return element2;
    }

}
