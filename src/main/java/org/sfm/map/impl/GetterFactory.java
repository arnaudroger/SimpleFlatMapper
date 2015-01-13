package org.sfm.map.impl;

import org.sfm.reflect.Getter;

import java.lang.reflect.Type;

public interface GetterFactory<T, K> {
	public <P> Getter<T, P> newGetter(final Type genericType, K key);
}
