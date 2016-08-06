package org.simpleflatmapper.reflect.primitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class FloatMethodSetter<T> implements FloatSetter<T> {

	private final Method method;
	
	public FloatMethodSetter(final Method method) {
		this.method = method;
	}

	@Override
	public void setFloat(final T target, final float value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

    @Override
    public String toString() {
        return "FloatMethodSetter{" +
                "method=" + method +
                '}';
    }
}
