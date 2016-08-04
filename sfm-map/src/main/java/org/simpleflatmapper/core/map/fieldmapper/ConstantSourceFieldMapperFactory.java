package org.simpleflatmapper.core.map.fieldmapper;

import org.simpleflatmapper.core.map.FieldKey;
import org.simpleflatmapper.core.map.FieldMapper;
import org.simpleflatmapper.core.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.core.map.column.FieldMapperColumnDefinition;
import org.simpleflatmapper.core.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.core.map.mapper.PropertyMapping;

public interface ConstantSourceFieldMapperFactory<S, K extends FieldKey<K>> {
	<T, P> FieldMapper<S, T> newFieldMapper(
			PropertyMapping<T, P, K, FieldMapperColumnDefinition<K>> propertyMapping,
			MappingContextFactoryBuilder contextFactoryBuilder,
			MapperBuilderErrorHandler mappingErrorHandler);
}
