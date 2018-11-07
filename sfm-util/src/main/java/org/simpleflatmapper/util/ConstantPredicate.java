package org.simpleflatmapper.util;

public class ConstantPredicate<T> implements Predicate<T>{

    private static final ConstantPredicate TRUE = new ConstantPredicate(true);

    private static final ConstantPredicate FALSE = new ConstantPredicate(false);

    public static <T> ConstantPredicate<T> truePredicate() {
        return TRUE;
    }

    public static <T> ConstantPredicate<T> falsePredicate() {
        return FALSE;
    }

    private final boolean b;

    private ConstantPredicate(boolean b) {
        this.b = b;
    }

    @Override
    public boolean test(T t) {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConstantPredicate<?> that = (ConstantPredicate<?>) o;

        return b == that.b;
    }

    @Override
    public int hashCode() {
        return (b ? 1 : 0);
    }
}
