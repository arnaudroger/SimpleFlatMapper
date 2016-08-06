package org.simpleflatmapper.reflect.primitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class CharacterMethodSetter<T> implements CharacterSetter<T> {

	private final Method method;
	
	public CharacterMethodSetter(final Method method) {
		this.method = method;
	}

	@Override
	public void setCharacter(final T target, final char value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

    @Override
    public String toString() {
        return "CharacterMethodSetter{" +
                "method=" + method +
                '}';
    }
}
