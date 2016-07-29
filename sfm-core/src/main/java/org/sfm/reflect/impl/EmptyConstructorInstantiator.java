package org.sfm.reflect.impl;

import org.sfm.reflect.Instantiator;
import org.sfm.utils.ErrorHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class EmptyConstructorInstantiator<S, T> implements Instantiator<S, T> {
	
	private final Constructor<? extends T> constructor;

	public EmptyConstructorInstantiator(final Constructor<? extends T> constructor) {
		this.constructor = constructor;
	}

	@Override
	public T newInstance(S s) throws Exception {
		try {
			return constructor.newInstance();
		} catch(InvocationTargetException e) {
			return ErrorHelper.rethrow(e.getCause());
		}
	}

    @Override
    public String toString() {
        return "EmptyConstructorInstantiator{" +
                "constructor=" + constructor +
                '}';
    }
}
