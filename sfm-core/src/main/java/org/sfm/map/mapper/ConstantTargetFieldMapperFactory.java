package org.sfm.map.mapper;

import org.sfm.map.FieldKey;
import org.sfm.map.FieldMapper;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.mapper.PropertyMapping;

public interface ConstantTargetFieldMapperFactory<T, K extends FieldKey<K>> {
	<S, P> FieldMapper<S, T> newFieldMapper(
			PropertyMapping<S, P, K, FieldMapperColumnDefinition<K>> propertyMapping,
			MappingContextFactoryBuilder contextFactoryBuilder,
			MapperBuilderErrorHandler mappingErrorHandler);
}
