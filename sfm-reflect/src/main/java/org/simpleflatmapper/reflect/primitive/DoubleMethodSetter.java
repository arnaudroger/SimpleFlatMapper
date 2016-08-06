package org.simpleflatmapper.reflect.primitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class DoubleMethodSetter<T> implements DoubleSetter<T> {

	private final Method method;
	
	public DoubleMethodSetter(final Method method) {
		this.method = method;
	}

	@Override
	public void setDouble(final T target, final double value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

    @Override
    public String toString() {
        return "DoubleMethodSetter{" +
                "method=" + method +
                '}';
    }
}
