package org.simpleflatmapper.tuple;

public class Tuple5<T1, T2, T3, T4, T5> extends Tuple4<T1, T2, T3, T4> {

    private final T5 element4;

    public Tuple5(T1 element0, T2 element1, T3 element2, T4 element3, T5 element4) {
        super(element0, element1, element2, element3);
        this.element4 = element4;
    }

    public final T5 getElement4() {
        return element4;
    }

    public final T5 fifth() {
        return getElement4();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Tuple5 tuple5 = (Tuple5) o;

        if (element4 != null ? !element4.equals(tuple5.element4) : tuple5.element4 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (element4 != null ? element4.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple5{" +
                "element0=" + getElement0() +
                ", element1=" + getElement1() +
                ", element2=" + getElement2() +
                ", element3=" + getElement3() +
                ", element4=" + getElement4() +
                '}';
    }

    public <T6> Tuple6<T1, T2, T3, T4, T5, T6> tuple6(T6 element5) {
        return new Tuple6<T1, T2, T3, T4, T5, T6>(getElement0(), getElement1(), getElement2(), getElement3(), getElement4(), element5);
    }
}
