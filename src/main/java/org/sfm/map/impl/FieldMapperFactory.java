package org.sfm.map.impl;

import org.sfm.map.*;

public interface FieldMapperFactory<S,  K extends FieldKey<K>, D extends ColumnDefinition<K, D>> {
	<T, P> FieldMapper<S, T> newFieldMapper(PropertyMapping<T, P, K, D> propertyMapping,  FieldMapperErrorHandler<K> errorHandler, MapperBuilderErrorHandler mapperErrorHandler);
}
