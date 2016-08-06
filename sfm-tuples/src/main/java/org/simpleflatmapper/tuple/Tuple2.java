package org.simpleflatmapper.tuple;

public class Tuple2<T1, T2> {

    private final T1 element0;
    private final T2 element1;

    public Tuple2(T1 element0, T2 element1) {
        this.element0 = element0;
        this.element1 = element1;
    }

    public final T1 getElement0() {
        return element0;
    }

    public final T1 first() {
        return getElement0();
    }

    public final T2 getElement1() {
        return element1;
    }

    public final T2 second() {
        return getElement1();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple2 tuple2 = (Tuple2) o;

        if (element0 != null ? !element0.equals(tuple2.element0) : tuple2.element0 != null) return false;
        if (element1 != null ? !element1.equals(tuple2.element1) : tuple2.element1 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = element0 != null ? element0.hashCode() : 0;
        result = 31 * result + (element1 != null ? element1.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple2{" +
                "element0=" + getElement0() +
                ", element1=" + getElement1() +
                '}';
    }

    public <T3> Tuple3<T1, T2, T3> tuple3(T3 element2) {
        return new Tuple3<T1, T2, T3>(getElement0(), getElement1(), element2);
    }
}
