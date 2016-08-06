package org.simpleflatmapper.tuple;

public class Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> extends Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> {

    private final T10 element9;

    public Tuple10(T1 element0, T2 element1, T3 element2, T4 element3, T5 element4, T6 element5, T7 element6, T8 element7, T9 element8, T10 element9) {
        super(element0, element1, element2, element3, element4, element5, element6, element7, element8);
        this.element9 = element9;
    }

    public final T10 getElement9() {
        return element9;
    }

    public final T10 tenth() {
        return getElement9();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Tuple10 tuple10 = (Tuple10) o;

        if (element9 != null ? !element9.equals(tuple10.element9) : tuple10.element9 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (element9 != null ? element9.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple10{" +
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
                '}';
    }

    public <T11> Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> tuple11(T11 element10) {
        return new Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>(getElement0(), getElement1(), getElement2(), getElement3(), getElement4(), getElement5(), getElement6(), getElement7(), getElement8(), getElement9(), element10);
    }
}
