package org.simpleflatmapper.reflect.primitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ShortMethodSetter<T> implements ShortSetter<T> {

	private final Method method;
	
	public ShortMethodSetter(final Method method) {
		this.method = method;
	}

	@Override
	public void setShort(final T target, final short value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

    @Override
    public String toString() {
        return "ShortMethodSetter{" +
                "method=" + method +
                '}';
    }
}
