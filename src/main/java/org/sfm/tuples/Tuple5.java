package org.sfm.tuples;

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
}
