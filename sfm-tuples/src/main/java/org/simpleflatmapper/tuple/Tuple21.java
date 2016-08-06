package org.simpleflatmapper.tuple;

public class Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> extends Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> {

    private final T21 element20;

    public Tuple21(T1 element0, T2 element1, T3 element2, T4 element3, T5 element4, T6 element5, T7 element6, T8 element7, T9 element8, T10 element9, T11 element10, T12 element11, T13 element12, T14 element13, T15 element14, T16 element15, T17 element16, T18 element17, T19 element18, T20 element19, T21 element20) {
        super(element0, element1, element2, element3, element4, element5, element6, element7, element8, element9, element10, element11, element12, element13, element14, element15, element16, element17, element18, element19);
        this.element20 = element20;
    }

    public final T21 getElement20() {
        return element20;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Tuple21 tuple21 = (Tuple21) o;

        if (element20 != null ? !element20.equals(tuple21.element20) : tuple21.element20 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (element20 != null ? element20.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple21{" +
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
                ", element15=" + getElement15() +
                ", element16=" + getElement16() +
                ", element17=" + getElement17() +
                ", element18=" + getElement18() +
                ", element19=" + getElement19() +
                ", element20=" + getElement20() +
                '}';
    }

    public <T22> Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> tuple22(T22 element21) {
        return new Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>(getElement0(), getElement1(), getElement2(), getElement3(), getElement4(), getElement5(), getElement6(), getElement7(), getElement8(), getElement9(), getElement10(), getElement11(), getElement12(), getElement13(), getElement14(), getElement15(), getElement16(), getElement17(), getElement18(), getElement19(), getElement20(), element21);
    }
}
