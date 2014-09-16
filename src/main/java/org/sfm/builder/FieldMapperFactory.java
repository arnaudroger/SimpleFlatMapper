package org.sfm.builder;

import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.reflect.Setter;

public interface FieldMapperFactory<S, K> {
	<T, P> FieldMapper<S, T> newFieldMapper(Setter<T, P> setter, K key, FieldMapperErrorHandler<K> errorHandler, MapperBuilderErrorHandler mapperErrorHandler);
}
