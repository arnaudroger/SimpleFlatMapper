package org.simpleflatmapper.map;

public interface SetRowMapper<ROW, SET, T, E extends Exception> extends ContextualSourceMapper<ROW, T>, EnumerableMapper<SET, T, E> {
}
