package org.sfm.reflect.primitive;

import java.lang.reflect.Field;

public final class DoubleFieldSetter<T> implements DoubleSetter<T> {

	private final Field field;
	
	public DoubleFieldSetter(Field field) {
		this.field = field;
	}

	@Override
	public void setDouble(T target, double value) throws IllegalArgumentException, IllegalAccessException {
		field.setDouble(target, value);
	}

}
