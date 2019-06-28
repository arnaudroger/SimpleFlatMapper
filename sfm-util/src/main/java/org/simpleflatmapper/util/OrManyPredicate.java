package org.simpleflatmapper.util;

import java.util.Arrays;

public final class OrManyPredicate<T> implements Predicate<T> {
    public final Predicate<? super T>[] predicates;

    public OrManyPredicate(Predicate<? super T>... predicates) {
        this.predicates = predicates;
    }

    @Override
    public boolean test(T t) {
        for (Predicate<? super T> predicate : predicates) {
            if (predicate.test(t)) return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrManyPredicate<?> that = (OrManyPredicate<?>) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(predicates, that.predicates);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(predicates);
    }

    @Override
    public String toString() {
        return "OrManyPredicate{" +
                "predicates=" + Arrays.toString(predicates) +
                '}';
    }
}
