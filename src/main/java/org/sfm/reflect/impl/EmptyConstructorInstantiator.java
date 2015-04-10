package org.sfm.reflect.impl;

import org.sfm.reflect.Instantiator;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public final class EmptyConstructorInstantiator<S, T> implements Instantiator<S, T> {
	
	private final Constructor<? extends T> constructor;

	public EmptyConstructorInstantiator(final Constructor<? extends T> constructor) {
		this.constructor = constructor;
	}

	@Override
	public T newInstance(S s) throws Exception {
		return constructor.newInstance();
	}

    @Override
    public String toString() {
        return "EmptyConstructorInstantiator{" +
                "constructor=" + constructor +
                '}';
    }
}
