package org.simpleflatmapper.tuple;

public class Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> extends Tuple7<T1, T2, T3, T4, T5, T6, T7> {

    private final T8 element7;

    public Tuple8(T1 element0, T2 element1, T3 element2, T4 element3, T5 element4, T6 element5, T7 element6, T8 element7) {
        super(element0, element1, element2, element3, element4, element5, element6);
        this.element7 = element7;
    }

    public final T8 getElement7() {
        return element7;
    }

    public final T8 eighth() {
        return getElement7();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Tuple8 tuple8 = (Tuple8) o;

        if (element7 != null ? !element7.equals(tuple8.element7) : tuple8.element7 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (element7 != null ? element7.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple8{" +
                "element0=" + getElement0() +
                ", element1=" + getElement1() +
                ", element2=" + getElement2() +
                ", element3=" + getElement3() +
                ", element4=" + getElement4() +
                ", element5=" + getElement5() +
                ", element6=" + getElement6() +
                ", element7=" + getElement7() +
                '}';
    }

    public <T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple9(T9 element8) {
        return new Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(getElement0(), getElement1(), getElement2(), getElement3(), getElement4(), getElement5(), getElement6(), getElement7(), element8);
    }
}
