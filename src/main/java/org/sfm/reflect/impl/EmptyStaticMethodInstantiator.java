package org.sfm.reflect.impl;

import org.sfm.reflect.Instantiator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class EmptyStaticMethodInstantiator<S, T> implements Instantiator<S, T> {

	private final Method method;
	private final Class<?> declaringClass;

	public EmptyStaticMethodInstantiator(final Method method) {
		this.method = method;
		this.declaringClass = method.getDeclaringClass();
	}

	@Override
	public T newInstance(S s) throws Exception {
		return (T) method.invoke(declaringClass);
	}

    @Override
    public String toString() {
        return "EmptyMethodInstantiator{}";
    }
}
