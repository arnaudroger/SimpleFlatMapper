package org.sfm.map.impl.fieldmapper;

import org.sfm.map.FieldKey;
import org.sfm.map.FieldMapper;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.mapper.PropertyMapping;

public interface ConstantSourceFieldMapperFactory<S, K extends FieldKey<K>> {
	<T, P> FieldMapper<S, T> newFieldMapper(
			PropertyMapping<T, P, K, FieldMapperColumnDefinition<K>> propertyMapping,
			MappingContextFactoryBuilder contextFactoryBuilder,
			MapperBuilderErrorHandler mappingErrorHandler);
}
