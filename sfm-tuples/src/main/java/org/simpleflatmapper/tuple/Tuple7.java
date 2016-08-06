package org.simpleflatmapper.tuple;

public class Tuple7<T1, T2, T3, T4, T5, T6, T7> extends Tuple6<T1, T2, T3, T4, T5, T6> {

    private final T7 element6;

    public Tuple7(T1 element0, T2 element1, T3 element2, T4 element3, T5 element4, T6 element5, T7 element6) {
        super(element0, element1, element2, element3, element4, element5);
        this.element6 = element6;
    }

    public final T7 getElement6() {
        return element6;
    }

    public final T7 seventh() {
        return getElement6();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Tuple7 tuple7 = (Tuple7) o;

        if (element6 != null ? !element6.equals(tuple7.element6) : tuple7.element6 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (element6 != null ? element6.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple7{" +
                "element0=" + getElement0() +
                ", element1=" + getElement1() +
                ", element2=" + getElement2() +
                ", element3=" + getElement3() +
                ", element4=" + getElement4() +
                ", element5=" + getElement5() +
                ", element6=" + getElement6() +
                '}';
    }

    public <T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> tuple8(T8 element7) {
        return new Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>(getElement0(), getElement1(), getElement2(), getElement3(), getElement4(), getElement5(), getElement6(), element7);
    }
}
