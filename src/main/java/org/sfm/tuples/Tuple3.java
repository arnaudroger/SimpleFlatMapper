package org.sfm.tuples;

public class Tuple3<T1, T2, T3> extends Tuple2<T1, T2> {

    private final T3 element2;

    public Tuple3(T1 element0, T2 element1, T3 element2) {
        super(element0, element1);
        this.element2 = element2;
    }

    public final T3 third() {
        return getElement2();
    }

    public final T3 getElement2() {
        return element2;
    }
}
