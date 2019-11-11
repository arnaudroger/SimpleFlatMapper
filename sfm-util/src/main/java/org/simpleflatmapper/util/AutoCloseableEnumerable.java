package org.simpleflatmapper.util;


public class AutoCloseableEnumerable<T> implements Enumerable<T>
//IFJAVA8_START
        , AutoCloseable
//IFJAVA8_END

{
    private final Enumerable<T> delegate;
    private final Closer closeable;

    public AutoCloseableEnumerable(Enumerable<T> delegate, Closer closeable) {
        this.delegate = delegate;
        this.closeable = closeable;
    }

    public void close() throws Exception {
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
