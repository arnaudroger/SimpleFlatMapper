package org.sfm.utils;

import org.sfm.map.MappingException;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class ForEachIteratorIterator<T> implements Iterator<T> {

    private final ForEachIterator<T> iterator;

    private boolean isFetched;
    private boolean hasValue;
    private T currentValue;

    public ForEachIteratorIterator(ForEachIterator<T> iterator) {
        this.iterator = iterator;
    }


    @Override
    public boolean hasNext() {
        fetch();
        return hasValue;
    }

    private void fetch() {
        if (!isFetched) {
            try {
                doFetch();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new MappingException(e.toString(), e);
            }
        }
    }

    private void doFetch() throws Exception {

        if (!iterator.next(new RowHandler<T>() {
            @Override
            public void handle(T t) throws Exception {
                currentValue = t;
                hasValue = true;
            }
        }))  {
            currentValue = null;
            hasValue = false;
        }
        isFetched = true;
    }

    @Override
    public T next() {
        fetch();
        if (hasValue) {
            T v = currentValue;
            currentValue = null;
            isFetched = false;
            return v;
        } else {
            throw new NoSuchElementException("No more rows");
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
