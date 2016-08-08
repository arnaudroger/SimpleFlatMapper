package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.PropertyMapping;

public interface ConstantSourceFieldMapperFactory<S, K extends FieldKey<K>> {
	<T, P> FieldMapper<S, T> newFieldMapper(
			PropertyMapping<T, P, K, FieldMapperColumnDefinition<K>> propertyMapping,
			MappingContextFactoryBuilder contextFactoryBuilder,
			MapperBuilderErrorHandler mappingErrorHandler);
}
