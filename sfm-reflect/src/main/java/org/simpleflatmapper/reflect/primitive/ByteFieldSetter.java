package org.simpleflatmapper.reflect.primitive;

import java.lang.reflect.Field;

public final class ByteFieldSetter<T> implements ByteSetter<T> {

	private final Field field;
	
	public ByteFieldSetter(final Field field) {
		this.field = field;
	}

	@Override
	public void setByte(final T target, final byte value) throws IllegalArgumentException, IllegalAccessException {
		field.setByte(target, value);
	}

    @Override
    public String toString() {
        return "ByteFieldSetter{" +
                "field=" + field +
                '}';
    }
}
