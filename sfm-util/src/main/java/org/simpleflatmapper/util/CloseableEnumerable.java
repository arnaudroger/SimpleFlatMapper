package org.simpleflatmapper.util;

import java.io.Closeable;
import java.io.IOException;

public class CloseableEnumerable<T> implements Enumerable<T>, Closeable {
    private final Enumerable<T> delegate;
    private final Closeable closeable;

    public CloseableEnumerable(Enumerable<T> delegate, Closeable closeable) {
        this.delegate = delegate;
        this.closeable = closeable;
    }

    @Override
    public void close() throws IOException {
        closeable.close();
    }

    @Override
    public boolean next() {
        return delegate.next();
    }

    @Override
    public T currentValue() {
        return delegate.currentValue();
    }
}
