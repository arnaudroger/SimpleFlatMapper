package org.sfm.reflect.primitive;

import java.lang.reflect.Field;

public final class LongFieldSetter<T> implements LongSetter<T> {

	private final Field field;
	
	public LongFieldSetter(Field field) {
		this.field = field;
	}

	@Override
	public void setLong(T target, long value) throws IllegalArgumentException, IllegalAccessException {
		field.setLong(target, value);
	}

}
