package org.sfm.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public class CloseableIterator<E> implements Iterator<E>, Closeable {
    private final Iterator<E> delegate;
    private final Closeable resource;

    public CloseableIterator(Iterator<E> delegate, Closeable resource) {
        this.resource = resource;
        this.delegate = delegate;
    }

    @Override
    public void close() throws IOException {
        resource.close();
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public E next() {
        return delegate.next();
    }
}
