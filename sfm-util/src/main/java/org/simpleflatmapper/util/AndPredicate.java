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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AndPredicate<?> that = (AndPredicate<?>) o;

        if (p1 != null ? !p1.equals(that.p1) : that.p1 != null) return false;
        return p2 != null ? p2.equals(that.p2) : that.p2 == null;
    }

    @Override
    public int hashCode() {
        int result = p1 != null ? p1.hashCode() : 0;
        result = 31 * result + (p2 != null ? p2.hashCode() : 0);
        return result;
    }
}
