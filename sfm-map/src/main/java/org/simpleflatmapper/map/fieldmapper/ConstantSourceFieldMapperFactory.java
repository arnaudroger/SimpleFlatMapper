package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.reflect.Getter;

public interface ConstantSourceFieldMapperFactory<S, K extends FieldKey<K>> {
	<T, P> FieldMapper<S, T> newFieldMapper(
			PropertyMapping<T, P, K, FieldMapperColumnDefinition<K>> propertyMapping,
			MappingContextFactoryBuilder contextFactoryBuilder,
			MapperBuilderErrorHandler mappingErrorHandler);

    @SuppressWarnings("unchecked")
    <T, P> Getter<? super S, ? extends P> getGetterFor(PropertyMapping<T, P, K, FieldMapperColumnDefinition<K>> propertyMapping);
}
