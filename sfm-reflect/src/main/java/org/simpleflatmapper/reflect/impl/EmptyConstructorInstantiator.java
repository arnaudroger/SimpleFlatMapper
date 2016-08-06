package org.simpleflatmapper.reflect.impl;

import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.util.ErrorHelper;

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
