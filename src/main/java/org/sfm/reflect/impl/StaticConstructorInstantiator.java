package org.sfm.reflect.impl;

import java.lang.reflect.Constructor;

import org.sfm.reflect.Instantiator;

public final class StaticConstructorInstantiator<S, T> implements Instantiator<S, T> {
	
	private final Constructor<T> constructor;
	private final Object[] args;
	
	public StaticConstructorInstantiator(final Constructor<T> constructor, final Object[] args) {
		this.constructor = constructor;
		this.args = args;
	}

	@Override
	public T newInstance(S s) throws Exception {
		return constructor.newInstance(args);
	}

}
