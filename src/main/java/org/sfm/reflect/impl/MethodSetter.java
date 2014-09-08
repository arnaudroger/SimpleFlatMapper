package org.sfm.reflect.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.sfm.reflect.Setter;

public final class MethodSetter<T, P> implements Setter<T, P> {

	private final Method method;
	private final Type type;
	
	public MethodSetter(final Method method) {
		this.method = method;
		this.type = method.getGenericParameterTypes()[0];
	}

	public void set(final T target, final P value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

	@Override
	public Type getPropertyType() {
		return type;
	}
	
	public Method getMethod() {
		return method;
	}
}
