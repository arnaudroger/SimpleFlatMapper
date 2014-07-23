package org.sfm.reflect.primitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class IntMethodSetter<T> implements IntSetter<T> {

	private final Method method;
	
	public IntMethodSetter(Method method) {
		this.method = method;
	}

	@Override
	public void setInt(T target, int value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

}
