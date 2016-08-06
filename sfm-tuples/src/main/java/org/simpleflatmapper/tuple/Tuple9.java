package org.simpleflatmapper.tuple;

public class Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> {

    private final T9 element8;

    public Tuple9(T1 element0, T2 element1, T3 element2, T4 element3, T5 element4, T6 element5, T7 element6, T8 element7, T9 element8) {
        super(element0, element1, element2, element3, element4, element5, element6, element7);
        this.element8 = element8;
    }

    public final T9 getElement8() {
        return element8;
    }

    public final T9 ninth() {
        return getElement8();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Tuple9 tuple9 = (Tuple9) o;

        if (element8 != null ? !element8.equals(tuple9.element8) : tuple9.element8 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (element8 != null ? element8.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple9{" +
                "element0=" + getElement0() +
                ", element1=" + getElement1() +
                ", element2=" + getElement2() +
                ", element3=" + getElement3() +
                ", element4=" + getElement4() +
                ", element5=" + getElement5() +
                ", element6=" + getElement6() +
                ", element7=" + getElement7() +
                ", element8=" + getElement8() +
                '}';
    }

    public <T10> Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> tuple10(T10 element9) {
        return new Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(getElement0(), getElement1(), getElement2(), getElement3(), getElement4(), getElement5(), getElement6(), getElement7(), getElement8(), element9);
    }
}
