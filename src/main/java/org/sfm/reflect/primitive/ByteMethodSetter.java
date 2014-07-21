package org.sfm.reflect.primitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ByteMethodSetter<T> implements ByteSetter<T> {

	private final Method method;
	
	public ByteMethodSetter(Method method) {
		this.method = method;
	}

	@Override
	public void setByte(T target, byte value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

}
