package org.simpleflatmapper.tuple;

public class Tuple3<T1, T2, T3> extends Tuple2<T1, T2> {

    private final T3 element2;

    public Tuple3(T1 element0, T2 element1, T3 element2) {
        super(element0, element1);
        this.element2 = element2;
    }

    public final T3 getElement2() {
        return element2;
    }

    public final T3 third() {
        return getElement2();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Tuple3 tuple3 = (Tuple3) o;

        if (element2 != null ? !element2.equals(tuple3.element2) : tuple3.element2 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (element2 != null ? element2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple3{" +
                "element0=" + getElement0() +
                ", element1=" + getElement1() +
                ", element2=" + getElement2() +
                '}';
    }

    public <T4> Tuple4<T1, T2, T3, T4> tuple4(T4 element3) {
        return new Tuple4<T1, T2, T3, T4>(getElement0(), getElement1(), getElement2(), element3);
    }
}
