package org.simpleflatmapper.jdbc.test;

import org.simpleflatmapper.util.CheckedConsumer;

public class KeyCapture<T> implements CheckedConsumer<T> {

    private T key;
    @Override
    public void accept(T t) throws Exception {
        key = t;
    }

    public T getKey() {
        return key;
    }
}
