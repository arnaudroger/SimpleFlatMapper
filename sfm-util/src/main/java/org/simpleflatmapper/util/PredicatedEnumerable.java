package org.simpleflatmapper.util;

public class PredicatedEnumerable<T> implements Enumerable<T> {

    private final Enumerable<? extends T> delegate;
    private final Predicate<? super T> predicate;
 
    public PredicatedEnumerable(Enumerable<? extends T> delegate, Predicate<? super T> predicate) {
        if (delegate instanceof PredicatedEnumerable) {
            PredicatedEnumerable<T> pe = (PredicatedEnumerable<T>) delegate;
            this.delegate = pe.delegate;
            this.predicate = new AndPredicate<T>(pe.predicate, predicate);
        } else {
            this.delegate = delegate;
            this.predicate = predicate;
        }
    }

    @Override
    public boolean next() {
        while (delegate.next()) {
            if (predicate.test(delegate.currentValue())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public T currentValue() {
        return delegate.currentValue();
    }
}
