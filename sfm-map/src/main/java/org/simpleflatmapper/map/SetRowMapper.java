package org.simpleflatmapper.map;

public interface SetRowMapper<ROW, SET, T, E extends Exception> extends SourceMapper<ROW, T>, EnumarableMapper<SET, T, E> {
}
