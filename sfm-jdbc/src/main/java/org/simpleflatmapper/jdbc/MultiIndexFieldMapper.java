package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.converter.Context;

import java.sql.PreparedStatement;

public interface MultiIndexFieldMapper<T> {
    int map(PreparedStatement ps, T value, int columnIndex, Context context) throws Exception;

    int getSize(T value);
}
