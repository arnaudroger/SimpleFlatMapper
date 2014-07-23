package org.sfm.reflect.primitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class LongMethodSetter<T> implements LongSetter<T> {

	private final Method method;
	
	public LongMethodSetter(Method method) {
		this.method = method;
	}

	@Override
	public void setLong(T target, long value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

}
