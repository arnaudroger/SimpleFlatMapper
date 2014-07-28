package org.sfm.jdbc;

import org.sfm.reflect.Instantiator;

public class SingletonInstantiator<T> implements Instantiator<T> {

	private final T instance;
	
	public SingletonInstantiator(T instance) {
		this.instance = instance;
	}

	@Override
	public T newInstance() throws Exception {
		return instance;
	}

}
