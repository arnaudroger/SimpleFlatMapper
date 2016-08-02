package org.simpleflatmapper.jdbc;

import java.sql.PreparedStatement;

public interface MultiIndexFieldMapper<T> {
    int map(PreparedStatement ps, T value, int columnIndex) throws Exception;

    int getSize(T value);
}
