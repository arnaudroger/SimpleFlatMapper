package org.sfm.utils;

public class FalsePredicate<T> implements Predicate<T> {

    public static final <T> Predicate<T> instance() {
        return new FalsePredicate<T>();
    }

    @Override
    public boolean test(T t) {
        return false;
    }
}
