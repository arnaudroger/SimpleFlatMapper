package org.sfm.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MethodSetter<T, P> implements Setter<T, P> {

	private final Method method;
	private final Class<? extends P> type;
	
	@SuppressWarnings("unchecked")
	public MethodSetter(final Method method) {
		this.method = method;
		this.type = (Class<? extends P>) method.getParameterTypes()[0];
	}

	public void set(final T target, final P value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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
