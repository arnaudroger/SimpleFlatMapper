package org.sfm.jdbc;

import org.sfm.utils.RowHandler;

public class KeyCapture<T> implements RowHandler<T> {

    private T key;
    @Override
    public void handle(T t) throws Exception {
        key = t;
    }

    public T getKey() {
        return key;
    }
}
