package org.simpleflatmapper.tuple;

public class Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> extends Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> {

    private final T11 element10;

    public Tuple11(T1 element0, T2 element1, T3 element2, T4 element3, T5 element4, T6 element5, T7 element6, T8 element7, T9 element8, T10 element9, T11 element10) {
        super(element0, element1, element2, element3, element4, element5, element6, element7, element8, element9);
        this.element10 = element10;
    }

    public final T11 getElement10() {
        return element10;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Tuple11 tuple11 = (Tuple11) o;

        if (element10 != null ? !element10.equals(tuple11.element10) : tuple11.element10 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (element10 != null ? element10.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple11{" +
                "element0=" + getElement0() +
                ", element1=" + getElement1() +
                ", element2=" + getElement2() +
                ", element3=" + getElement3() +
                ", element4=" + getElement4() +
                ", element5=" + getElement5() +
                ", element6=" + getElement6() +
                ", element7=" + getElement7() +
                ", element8=" + getElement8() +
                ", element9=" + getElement9() +
                ", element10=" + getElement10() +
                '}';
    }

    public <T12> Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> tuple12(T12 element11) {
        return new Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>(getElement0(), getElement1(), getElement2(), getElement3(), getElement4(), getElement5(), getElement6(), getElement7(), getElement8(), getElement9(), getElement10(), element11);
    }
}
