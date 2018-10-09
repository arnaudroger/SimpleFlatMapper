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
}
