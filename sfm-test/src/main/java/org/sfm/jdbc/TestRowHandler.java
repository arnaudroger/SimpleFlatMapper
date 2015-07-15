package org.sfm.jdbc;

public interface TestRowHandler<T> {
    void handle(T t) throws Exception;
}
