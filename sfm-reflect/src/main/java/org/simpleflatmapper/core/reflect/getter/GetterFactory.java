package org.simpleflatmapper.core.reflect.getter;

import org.simpleflatmapper.core.reflect.Getter;

import java.lang.reflect.Type;

public interface GetterFactory<T, K> {
	<P> Getter<T, P> newGetter(final Type target, K key, Object... properties);
}
