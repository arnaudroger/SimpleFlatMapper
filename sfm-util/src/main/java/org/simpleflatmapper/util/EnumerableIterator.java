package org.simpleflatmapper.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EnumerableIterator<T> implements Iterator<T> {

    private final Enumerable<T> enumerable;

    private boolean fetch = false;
    private boolean hasValue = false;

    public EnumerableIterator(Enumerable<T> enumerable) {
        this.enumerable = enumerable;
    }

    @Override
    public boolean hasNext() {
        if (!fetch) {
            fetch();
        }
        return hasValue;
    }

    private void fetch() {
        hasValue = enumerable.next();
        fetch = true;
    }

    @Override
    public T next() {
        if (!fetch) {
            fetch();
        }

        if (hasValue) {
            fetch = false;
            hasValue = false;

            return enumerable.currentValue();
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
