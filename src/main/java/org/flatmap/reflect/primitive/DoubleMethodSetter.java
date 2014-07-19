package org.flatmap.reflect.primitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DoubleMethodSetter<T> implements DoubleSetter<T> {

	private final Method method;
	
	public DoubleMethodSetter(Method method) {
		this.method = method;
	}

	@Override
	public void setDouble(T target, double value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

}
