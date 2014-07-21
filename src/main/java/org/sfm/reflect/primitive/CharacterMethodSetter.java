package org.sfm.reflect.primitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CharacterMethodSetter<T> implements CharacterSetter<T> {

	private final Method method;
	
	public CharacterMethodSetter(Method method) {
		this.method = method;
	}

	@Override
	public void setCharacter(T target, char value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

}
