package org.simpleflatmapper.map;

public interface SetRowMapper<ROW, SET, T, E extends Exception> extends Mapper<ROW, T>, EnumarableMapper<SET, T, E> {
    MappingContext<? super ROW> newMappingContext(ROW row) throws E;
}
