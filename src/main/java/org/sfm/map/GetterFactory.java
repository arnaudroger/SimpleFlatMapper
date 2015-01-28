package org.sfm.map;

import org.sfm.reflect.Getter;

import java.lang.reflect.Type;

public interface GetterFactory<T, K extends FieldKey<K>> {
	public <P> Getter<T, P> newGetter(final Type target, K key);
}
