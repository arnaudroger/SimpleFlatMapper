package org.sfm.reflect.primitive;

import java.lang.reflect.Field;

public final class FloatFieldSetter<T> implements FloatSetter<T> {

	private final Field field;
	
	public FloatFieldSetter(final Field field) {
		this.field = field;
	}

	@Override
	public void setFloat(final T target, final float value) throws IllegalArgumentException, IllegalAccessException {
		field.setFloat(target, value);
	}

}
