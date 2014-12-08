package org.sfm.tuples;

public class Tuple2<T1, T2> {

    private final T1 element0;
    private final T2 element1;

    public Tuple2(T1 element0, T2 element1) {
        this.element0 = element0;
        this.element1 = element1;
    }

    public final T1 first() {
        return getElement0();
    }

    public final T2 second() {
        return getElement1();
    }

    public final T1 getElement0() {
        return element0;
    }

    public final T2 getElement1() {
        return element1;
    }

}
