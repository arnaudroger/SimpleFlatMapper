package org.simpleflatmapper.reflect.primitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ByteMethodSetter<T> implements ByteSetter<T> {

	private final Method method;
	
	public ByteMethodSetter(final Method method) {
		this.method = method;
	}

	@Override
	public void setByte(final T target, final byte value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

    @Override
    public String toString() {
        return "ByteMethodSetter{" +
                "method=" + method +
                '}';
    }
}
