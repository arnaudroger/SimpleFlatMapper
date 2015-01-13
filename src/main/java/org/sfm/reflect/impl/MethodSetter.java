package org.sfm.reflect.impl;

import org.sfm.reflect.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MethodSetter<T, P> implements Setter<T, P> {

	private final Method method;
	
	public MethodSetter(final Method method) {
		this.method = method;
	}

	public void set(final T target, final P value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

	public Method getMethod() {
		return method;
	}
}
