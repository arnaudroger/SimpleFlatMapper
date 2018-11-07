package org.simpleflatmapper.util;

public class EqualsPredicate<T> implements Predicate<T>{

    public final T expected;

    private EqualsPredicate(T expected) {
        this.expected = expected;
    }

    @Override
    public boolean test(T t) {
        return expected == null ? t == null : expected.equals(t);
    }
    
    
    public static <T> EqualsPredicate<T> of(T value) {
        return new EqualsPredicate<T>(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EqualsPredicate<?> that = (EqualsPredicate<?>) o;

        return expected != null ? expected.equals(that.expected) : that.expected == null;
    }

    @Override
    public int hashCode() {
        return expected != null ? expected.hashCode() : 0;
    }
}
