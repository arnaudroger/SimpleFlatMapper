package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;

import java.lang.reflect.Type;

public interface GetterFactory<T, K> {
	<P> Getter<T, P> newGetter(final Type target, K key, Object... properties);
}
