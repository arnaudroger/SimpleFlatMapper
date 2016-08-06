package org.simpleflatmapper.reflect.primitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class LongMethodSetter<T> implements LongSetter<T> {

	private final Method method;
	
	public LongMethodSetter(final Method method) {
		this.method = method;
	}

	@Override
	public void setLong(final T target, final long value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

    @Override
    public String toString() {
        return "LongMethodSetter{" +
                "method=" + method +
                '}';
    }
}
