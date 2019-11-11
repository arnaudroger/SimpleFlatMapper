package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.mapper.ColumnDefinition;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.Supplier;

import java.lang.reflect.Type;

public interface ConstantSourceFieldMapperFactory<S, K extends FieldKey<K>> {
	<T, P> FieldMapper<S, T> newFieldMapper(
			PropertyMapping<T, P, K> propertyMapping,
			MappingContextFactoryBuilder contextFactoryBuilder,
			MapperBuilderErrorHandler mappingErrorHandler);

    <P> ContextualGetter<? super S, ? extends P> getGetterFromSource(
    		K columnKey,
			Type propertyType,
			ColumnDefinition<K, ?> columnDefinition,
			Supplier<ClassMeta<P>> propertyClassMetaSupplier,
			MappingContextFactoryBuilder<?, ? extends FieldKey<?>> contextFactoryBuilder
			);
}
