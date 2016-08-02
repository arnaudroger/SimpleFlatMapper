package org.simpleflatmapper.test.jdbc;

public interface TestRowHandler<T> {
    void handle(T t) throws Exception;
}
