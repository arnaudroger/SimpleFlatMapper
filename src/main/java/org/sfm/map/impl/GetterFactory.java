package org.sfm.map.impl;

import java.lang.reflect.Type;

import org.sfm.reflect.Getter;

public interface GetterFactory<T, K> {
	public <P> Getter<T, P> newGetter(final Type genericType, K key);
}
