package org.sfm.reflect.primitive;

import java.lang.reflect.Field;

public class IntFieldSetter<T> implements IntSetter<T> {

	private final Field field;
	
	public IntFieldSetter(Field field) {
		this.field = field;
	}

	@Override
	public void setInt(T target, int value) throws IllegalArgumentException, IllegalAccessException {
		field.setInt(target, value);
	}

}
