package org.sfm.map.impl;

import org.sfm.map.ColumnDefinition;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.reflect.Setter;

import java.lang.reflect.Type;

public interface FieldMapperFactory<S,  K extends FieldKey<K>, D extends ColumnDefinition<K>> {
	<T, P> FieldMapper<S, T> newFieldMapper(PropertyMapping<T, P, K, D> propertyMapping, FieldMapperErrorHandler<K> errorHandler, MapperBuilderErrorHandler mapperErrorHandler);
}
