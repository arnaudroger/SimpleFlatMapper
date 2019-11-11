package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.getter.ContextualGetter;

import java.lang.reflect.Type;

public interface ContextualGetterFactory<T, K> {
	<P> ContextualGetter<T, P> newGetter(final Type target, K key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties);
}
