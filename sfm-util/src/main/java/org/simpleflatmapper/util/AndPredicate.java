package org.simpleflatmapper.util;

public final class AndPredicate<T> implements Predicate<T> {
    public final Predicate<? super T> p1;
    public final Predicate<? super T> p2;

    public AndPredicate(Predicate<? super T> p1, Predicate<? super T> p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public boolean test(T t) {
        return p1.test(t) && p2.test(t);
    }
}
