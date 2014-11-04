package org.sfm.map.impl;

import java.lang.reflect.Type;

import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.reflect.Setter;

public interface FieldMapperFactory<S, K> {
	<T, P> FieldMapper<S, T> newFieldMapper(Type propertyType, Setter<T, P> setter, K key, FieldMapperErrorHandler<K> errorHandler, MapperBuilderErrorHandler mapperErrorHandler);
}
