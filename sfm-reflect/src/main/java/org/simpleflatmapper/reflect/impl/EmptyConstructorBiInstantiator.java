package org.simpleflatmapper.reflect.impl;

import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.util.ErrorHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class EmptyConstructorBiInstantiator<S1, S2, T> implements BiInstantiator<S1, S2, T> {

	private final Constructor<? extends T> constructor;

	public EmptyConstructorBiInstantiator(final Constructor<? extends T> constructor) {
		this.constructor = constructor;
	}

	@Override
	public T newInstance(S1 s1, S2 s2) throws Exception {
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
