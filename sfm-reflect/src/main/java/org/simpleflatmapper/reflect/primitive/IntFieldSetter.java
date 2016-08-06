package org.simpleflatmapper.reflect.primitive;

import java.lang.reflect.Field;

public final class IntFieldSetter<T> implements IntSetter<T> {

	private final Field field;
	
	public IntFieldSetter(final Field field) {
		this.field = field;
	}

	@Override
	public void setInt(final T target, final int value) throws IllegalArgumentException, IllegalAccessException {
		field.setInt(target, value);
	}

    @Override
    public String toString() {
        return "IntFieldSetter{" +
                "field=" + field +
                '}';
    }
}
