package org.sfm.tuples;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Tuple5<T1, T2, T3, T4, T5>  extends Tuple4<T1, T2, T3, T4> {

    private final T5 element4;

    public Tuple5(T1 element0, T2 element1, T3 element2, T4 element3, T5 element4) {
        super(element0, element1, element2, element3);
        this.element4 = element4;
    }

    public final T5 fifth() {
        return getElement4();
    }

    public final T5 getElement4() {
        return element4;
    }
}
