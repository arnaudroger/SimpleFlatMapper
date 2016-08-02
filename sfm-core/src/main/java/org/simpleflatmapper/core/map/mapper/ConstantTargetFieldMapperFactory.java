package org.simpleflatmapper.core.map.mapper;

import org.simpleflatmapper.core.map.FieldKey;
import org.simpleflatmapper.core.map.FieldMapper;
import org.simpleflatmapper.core.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.core.map.column.FieldMapperColumnDefinition;
import org.simpleflatmapper.core.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.core.map.mapper.PropertyMapping;

public interface ConstantTargetFieldMapperFactory<T, K extends FieldKey<K>> {
	<S, P> FieldMapper<S, T> newFieldMapper(
			PropertyMapping<S, P, K, FieldMapperColumnDefinition<K>> propertyMapping,
			MappingContextFactoryBuilder contextFactoryBuilder,
			MapperBuilderErrorHandler mappingErrorHandler);
}
