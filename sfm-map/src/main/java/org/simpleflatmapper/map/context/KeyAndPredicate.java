package org.simpleflatmapper.map.context;

import org.simpleflatmapper.util.Predicate;

public class KeyAndPredicate<S, K> implements Predicate<S> {
    public final K key;
    public final Predicate<? super S> predicate;

    public KeyAndPredicate(K key, Predicate<? super S> predicate) {
        this.key = key;
        this.predicate = predicate;
    }

    @Override
    public boolean test(S s) {
        return predicate == null ? true : predicate.test(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyAndPredicate<?, ?> that = (KeyAndPredicate<?, ?>) o;

        if (!key.equals(that.key)) return false;
        return predicate != null ? predicate.equals(that.predicate) : that.predicate == null;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + (predicate != null ? predicate.hashCode() : 0);
        return result;
    }

    public KeyAndPredicate<S, K> mergeWith(KeyAndPredicate<S, K> keyAndPredicate) {
        if (!this.key.equals(keyAndPredicate.key)) throw new IllegalArgumentException();
        
        if (this.predicate == null || keyAndPredicate.predicate == null) { // null is equivalent to true
            return this;
        }
        
        if (this.predicate.equals(keyAndPredicate.predicate)) {
            return this;
        }
        
        return new KeyAndPredicate<S, K>(key, new OrPredicate<S>(predicate, keyAndPredicate.predicate));
    }

    private static class OrPredicate<S> implements Predicate<S> {
        private final Predicate<? super S> p1;
        private final Predicate<? super S> p2;

        private OrPredicate(Predicate<? super S> p1, Predicate<? super S> p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        @Override
        public boolean test(S s) {
            return p1.test(s) || p2.test(s);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OrPredicate<?> that = (OrPredicate<?>) o;

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
}
