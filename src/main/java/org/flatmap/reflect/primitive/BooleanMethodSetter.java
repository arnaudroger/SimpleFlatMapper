package org.flatmap.reflect.primitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BooleanMethodSetter<T> implements BooleanSetter<T> {

	private final Method method;
	
	public BooleanMethodSetter(Method method) {
		this.method = method;
	}

	@Override
	public void setBoolean(T target, boolean value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

}
