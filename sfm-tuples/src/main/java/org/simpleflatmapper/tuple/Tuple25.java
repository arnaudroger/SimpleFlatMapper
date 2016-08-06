package org.simpleflatmapper.tuple;

public class Tuple25<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25> extends Tuple24<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24> {

    private final T25 element24;

    public Tuple25(T1 element0, T2 element1, T3 element2, T4 element3, T5 element4, T6 element5, T7 element6, T8 element7, T9 element8, T10 element9, T11 element10, T12 element11, T13 element12, T14 element13, T15 element14, T16 element15, T17 element16, T18 element17, T19 element18, T20 element19, T21 element20, T22 element21, T23 element22, T24 element23, T25 element24) {
        super(element0, element1, element2, element3, element4, element5, element6, element7, element8, element9, element10, element11, element12, element13, element14, element15, element16, element17, element18, element19, element20, element21, element22, element23);
        this.element24 = element24;
    }

    public final T25 getElement24() {
        return element24;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Tuple25 tuple25 = (Tuple25) o;

        if (element24 != null ? !element24.equals(tuple25.element24) : tuple25.element24 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (element24 != null ? element24.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple25{" +
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
                ", element21=" + getElement21() +
                ", element22=" + getElement22() +
                ", element23=" + getElement23() +
                ", element24=" + getElement24() +
                '}';
    }

    public <T26> Tuple26<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26> tuple26(T26 element25) {
        return new Tuple26<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26>(getElement0(), getElement1(), getElement2(), getElement3(), getElement4(), getElement5(), getElement6(), getElement7(), getElement8(), getElement9(), getElement10(), getElement11(), getElement12(), getElement13(), getElement14(), getElement15(), getElement16(), getElement17(), getElement18(), getElement19(), getElement20(), getElement21(), getElement22(), getElement23(), getElement24(), element25);
    }
}
