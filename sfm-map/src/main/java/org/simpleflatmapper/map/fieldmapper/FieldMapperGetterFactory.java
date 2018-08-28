package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.reflect.Getter;

import java.lang.reflect.Type;

public interface FieldMapperGetterFactory<T, K> {
	<P> FieldMapperGetter<T, P> newGetter(final Type target, K key, MappingContextFactoryBuilder<?, K> mappingContextFactoryBuilder, Object... properties);
}
