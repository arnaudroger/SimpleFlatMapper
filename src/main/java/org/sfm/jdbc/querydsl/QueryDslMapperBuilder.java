package org.sfm.jdbc.querydsl;

import org.sfm.jdbc.MapperBuilder;
import org.sfm.map.Mapper;

import com.mysema.query.Tuple;
import com.mysema.query.types.Expression;

public interface QueryDslMapperBuilder<T> extends MapperBuilder<Tuple, T, TupleElementKey<?>, Mapper<Tuple, T>,  QueryDslMapperBuilder<T>> {
	public <E> QueryDslMapperBuilder<T> addMapping(Expression<E> expr);
	public <E> QueryDslMapperBuilder<T> addMapping(Expression<E> expression, int index);
}