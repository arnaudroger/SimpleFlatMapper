package org.sfm.reflect.primitive;

import java.lang.reflect.Field;

public class BooleanFieldSetter<T> implements BooleanSetter<T> {

	private final Field field;
	
	public BooleanFieldSetter(Field field) {
		this.field = field;
	}

	@Override
	public void setBoolean(T target, boolean value) throws IllegalArgumentException, IllegalAccessException {
		field.setBoolean(target, value);
	}

}
