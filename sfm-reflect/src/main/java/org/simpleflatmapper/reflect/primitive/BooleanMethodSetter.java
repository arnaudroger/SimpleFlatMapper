package org.simpleflatmapper.reflect.primitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class BooleanMethodSetter<T> implements BooleanSetter<T> {

	private final Method method;
	
	public BooleanMethodSetter(final Method method) {
		this.method = method;
	}

	@Override
	public void setBoolean(final T target, final boolean value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

    @Override
    public String toString() {
        return "BooleanMethodSetter{" +
                "method=" + method +
                '}';
    }
}
