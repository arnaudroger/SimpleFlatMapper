package org.flatmap.reflect.primitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ShortMethodSetter<T> implements ShortSetter<T> {

	private final Method method;
	
	public ShortMethodSetter(Method method) {
		this.method = method;
	}

	@Override
	public void setShort(T target, short value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

}
