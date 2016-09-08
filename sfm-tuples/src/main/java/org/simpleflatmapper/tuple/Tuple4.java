package org.simpleflatmapper.tuple;

public class Tuple4<T1, T2, T3, T4> extends Tuple3<T1, T2, T3> {

    private final T4 element3;

    public Tuple4(T1 element0, T2 element1, T3 element2, T4 element3) {
        super(element0, element1, element2);
        this.element3 = element3;
    }

    public final T4 getElement3() {
        return element3;
    }

    public final T4 fourth() {
        return getElement3();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Tuple4 tuple4 = (Tuple4) o;

        if (element3 != null ? !element3.equals(tuple4.element3) : tuple4.element3 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (element3 != null ? element3.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple4{" +
                "element0=" + getElement0() +
                ", element1=" + getElement1() +
                ", element2=" + getElement2() +
                ", element3=" + getElement3() +
                '}';
    }

    public <T5> Tuple5<T1, T2, T3, T4, T5> tuple5(T5 element4) {
        return new Tuple5<T1, T2, T3, T4, T5>(getElement0(), getElement1(), getElement2(), getElement3(), element4);
    }
}
