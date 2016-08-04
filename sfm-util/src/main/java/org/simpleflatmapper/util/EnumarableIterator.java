package org.simpleflatmapper.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EnumarableIterator<T> implements Iterator<T> {

    private final Enumarable<T> enumarable;

    private boolean fetch = false;
    private boolean hasValue = false;

    public EnumarableIterator(Enumarable<T> enumarable) {
        this.enumarable = enumarable;
    }

    @Override
    public boolean hasNext() {
        if (!fetch) {
            fetch();
        }
        return hasValue;
    }

    private void fetch() {
        hasValue = enumarable.next();
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

            return enumarable.currentValue();
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
