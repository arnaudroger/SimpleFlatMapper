package org.sfm.reflect;

import java.lang.reflect.Constructor;

public final class ConstructorInstantiator<T> implements Instantiator<T> {
	
	private final Constructor<T> constructor;
	private final Object[] args;
	
	public ConstructorInstantiator(final Constructor<T> constructor, final Object[] args) {
		this.constructor = constructor;
		this.args = args;
	}

	@Override
	public T newInstance() throws Exception {
		return constructor.newInstance(args);
	}

}
