package org.flatmap.reflect.primitive;

import java.lang.reflect.Field;

public class FloatFieldSetter<T> implements FloatSetter<T> {

	private final Field field;
	
	public FloatFieldSetter(Field field) {
		this.field = field;
	}

	@Override
	public void setFloat(T target, float value) throws IllegalArgumentException, IllegalAccessException {
		field.setFloat(target, value);
	}

}
