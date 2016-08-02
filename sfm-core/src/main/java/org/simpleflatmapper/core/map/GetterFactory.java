package org.simpleflatmapper.core.map;

import org.simpleflatmapper.core.map.mapper.ColumnDefinition;
import org.simpleflatmapper.core.reflect.Getter;

import java.lang.reflect.Type;

public interface GetterFactory<T, K extends FieldKey<K>> {
	<P> Getter<T, P> newGetter(final Type target, K key, ColumnDefinition<?, ?> columnDefinition);
}
