package org.flatmap.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodSetter<T, P> implements Setter<T, P> {

	private final Method method;
	
	public MethodSetter(Method method) {
		this.method = method;
	}

	public void set(T target, P value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}
}
