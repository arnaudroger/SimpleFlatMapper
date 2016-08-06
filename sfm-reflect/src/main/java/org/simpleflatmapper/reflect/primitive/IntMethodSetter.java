package org.simpleflatmapper.reflect.primitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class IntMethodSetter<T> implements IntSetter<T> {

	private final Method method;
	
	public IntMethodSetter(final Method method) {
		this.method = method;
	}

	@Override
	public void setInt(final T target, final int value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

    @Override
    public String toString() {
        return "IntMethodSetter{" +
                "method=" + method +
                '}';
    }
}
