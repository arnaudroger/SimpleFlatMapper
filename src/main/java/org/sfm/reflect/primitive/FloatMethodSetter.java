package org.sfm.reflect.primitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class FloatMethodSetter<T> implements FloatSetter<T> {

	private final Method method;
	
	public FloatMethodSetter(Method method) {
		this.method = method;
	}

	@Override
	public void setFloat(T target, float value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

}
