package org.sfm.reflect.primitive;

import java.lang.reflect.Field;

public final class ByteFieldSetter<T> implements ByteSetter<T> {

	private final Field field;
	
	public ByteFieldSetter(Field field) {
		this.field = field;
	}

	@Override
	public void setByte(T target, byte value) throws IllegalArgumentException, IllegalAccessException {
		field.setByte(target, value);
	}

}
