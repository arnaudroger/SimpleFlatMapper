package org.flatmap.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodSetter<T, P> implements Setter<T, P> {

	private final Method method;
	private final Class<? extends P> type;
	
	@SuppressWarnings("unchecked")
	public MethodSetter(Method method) {
		this.method = method;
		this.type = (Class<? extends P>) method.getParameterTypes()[0];
	}

	public void set(T target, P value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

	@Override
	public Class<? extends P> getPropertyType() {
		return type;
	}
	
	public Method getMethod() {
		return method;
	}
}
