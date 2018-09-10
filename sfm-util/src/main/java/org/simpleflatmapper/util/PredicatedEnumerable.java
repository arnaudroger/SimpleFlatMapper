package org.simpleflatmapper.util;

public class PredicatedEnumerable<T> implements Enumerable<T> {

    private final Enumerable<T> delegate;
    private final Predicate<T> predicate;
 
    public PredicatedEnumerable(Enumerable<T> delegate, Predicate<T> predicate) {
        this.delegate = delegate;
        this.predicate = predicate;
    }

    @Override
    public boolean next() {
        while (delegate.next()) {
            if (predicate.test(delegate.currentValue())) return true;
        }
        return false;
    }

    @Override
    public T currentValue() {
        return delegate.currentValue();
    }
}
