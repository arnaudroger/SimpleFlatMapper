package org.simpleflatmapper.util;

import java.util.Iterator;

public class AutoCloseableIterator<E> implements Iterator<E>
//IFJAVA8_START
        , AutoCloseable
//IFJAVA8_END
{
    private final Iterator<E> delegate;
    private final Closer resource;

    public AutoCloseableIterator(Iterator<E> delegate, Closer resource) {
        this.resource = resource;
        this.delegate = delegate;
    }

    public void close() throws Exception {
        resource.close();
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public void remove() {
        delegate.remove();
    }

    @Override
    public E next() {
        return delegate.next();
    }


}
