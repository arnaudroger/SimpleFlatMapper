package org.sfm.reflect;

import java.lang.reflect.Constructor;

public class ConstructorInstantiator<T> implements Instantiator<T> {
	private static final Object[] EMPTY_ARGS = new Object[] {};
	
	private final Constructor<T> constructor;
	private final Object[] args;
	
	public ConstructorInstantiator(Constructor<T> constructor, Object[] args) {
		this.constructor = constructor;
		this.args = args;
	}
	public ConstructorInstantiator(Constructor<T> constructor) {
		this.constructor = constructor;
		this.args = EMPTY_ARGS;
	}

	@Override
	public T newInstance() throws Exception {
		return constructor.newInstance(args);
	}

}
