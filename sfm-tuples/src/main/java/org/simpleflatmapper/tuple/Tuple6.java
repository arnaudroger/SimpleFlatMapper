package org.simpleflatmapper.tuple;

public class Tuple6<T1, T2, T3, T4, T5, T6> extends Tuple5<T1, T2, T3, T4, T5> {

    private final T6 element5;

    public Tuple6(T1 element0, T2 element1, T3 element2, T4 element3, T5 element4, T6 element5) {
        super(element0, element1, element2, element3, element4);
        this.element5 = element5;
    }

    public final T6 getElement5() {
        return element5;
    }

    public final T6 sixth() {
        return getElement5();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Tuple6 tuple6 = (Tuple6) o;

        if (element5 != null ? !element5.equals(tuple6.element5) : tuple6.element5 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (element5 != null ? element5.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple6{" +
                "element0=" + getElement0() +
                ", element1=" + getElement1() +
                ", element2=" + getElement2() +
                ", element3=" + getElement3() +
                ", element4=" + getElement4() +
                ", element5=" + getElement5() +
                '}';
    }

    public <T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple7(T7 element6) {
        return new Tuple7<T1, T2, T3, T4, T5, T6, T7>(getElement0(), getElement1(), getElement2(), getElement3(), getElement4(), getElement5(), element6);
    }
}
