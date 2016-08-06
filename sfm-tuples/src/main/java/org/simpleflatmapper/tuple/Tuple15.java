package org.simpleflatmapper.tuple;

public class Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> extends Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> {

    private final T15 element14;

    public Tuple15(T1 element0, T2 element1, T3 element2, T4 element3, T5 element4, T6 element5, T7 element6, T8 element7, T9 element8, T10 element9, T11 element10, T12 element11, T13 element12, T14 element13, T15 element14) {
        super(element0, element1, element2, element3, element4, element5, element6, element7, element8, element9, element10, element11, element12, element13);
        this.element14 = element14;
    }

    public final T15 getElement14() {
        return element14;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Tuple15 tuple15 = (Tuple15) o;

        if (element14 != null ? !element14.equals(tuple15.element14) : tuple15.element14 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (element14 != null ? element14.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple15{" +
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
                ", element11=" + getElement11() +
                ", element12=" + getElement12() +
                ", element13=" + getElement13() +
                ", element14=" + getElement14() +
                '}';
    }

    public <T16> Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> tuple16(T16 element15) {
        return new Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>(getElement0(), getElement1(), getElement2(), getElement3(), getElement4(), getElement5(), getElement6(), getElement7(), getElement8(), getElement9(), getElement10(), getElement11(), getElement12(), getElement13(), getElement14(), element15);
    }
}
