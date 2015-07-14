package org.sfm.map;


public interface SetRowMapper<R, S, T, E extends Exception> extends Mapper<R, T>, EnumarableMapper<S, T, E> {
    MappingContext<? super S> newMappingContext(S rs) throws E;
}
