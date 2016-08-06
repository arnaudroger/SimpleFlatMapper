package org.simpleflatmapper.tuple;

public class Tuple32<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32> extends Tuple31<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31> {

    private final T32 element31;

    public Tuple32(T1 element0, T2 element1, T3 element2, T4 element3, T5 element4, T6 element5, T7 element6, T8 element7, T9 element8, T10 element9, T11 element10, T12 element11, T13 element12, T14 element13, T15 element14, T16 element15, T17 element16, T18 element17, T19 element18, T20 element19, T21 element20, T22 element21, T23 element22, T24 element23, T25 element24, T26 element25, T27 element26, T28 element27, T29 element28, T30 element29, T31 element30, T32 element31) {
        super(element0, element1, element2, element3, element4, element5, element6, element7, element8, element9, element10, element11, element12, element13, element14, element15, element16, element17, element18, element19, element20, element21, element22, element23, element24, element25, element26, element27, element28, element29, element30);
        this.element31 = element31;
    }

    public final T32 getElement31() {
        return element31;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Tuple32 tuple32 = (Tuple32) o;

        if (element31 != null ? !element31.equals(tuple32.element31) : tuple32.element31 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (element31 != null ? element31.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple32{" +
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
                ", element25=" + getElement25() +
                ", element26=" + getElement26() +
                ", element27=" + getElement27() +
                ", element28=" + getElement28() +
                ", element29=" + getElement29() +
                ", element30=" + getElement30() +
                ", element31=" + getElement31() +
                '}';
    }
}
