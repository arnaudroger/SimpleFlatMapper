package org.sfm.tuples;

public class Tuple4<T1, T2, T3, T4>  extends Tuple3<T1, T2, T3> {

    private final T4 element3;

    public Tuple4(T1 element0, T2 element1, T3 element2, T4 element3) {
        super(element0, element1, element2);
        this.element3 = element3;
    }

    public final T4 forth() {
        return getElement3();
    }

    public final T4 getElement3() {
        return element3;
    }
}
