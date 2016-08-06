package org.simpleflatmapper.map;

public interface SetRowMapper<R, S, T, E extends Exception> extends Mapper<R, T>, EnumarableMapper<S, T, E> {
    MappingContext<? super R> newMappingContext(R rs) throws E;
}
