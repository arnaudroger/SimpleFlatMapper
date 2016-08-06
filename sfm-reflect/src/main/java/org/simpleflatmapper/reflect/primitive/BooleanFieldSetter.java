package org.simpleflatmapper.reflect.primitive;

import java.lang.reflect.Field;

public final class BooleanFieldSetter<T> implements BooleanSetter<T> {

	private final Field field;
	
	public BooleanFieldSetter(final Field field) {
		this.field = field;
	}

	@Override
	public void setBoolean(final T target, final boolean value) throws IllegalArgumentException, IllegalAccessException {
		field.setBoolean(target, value);
	}

    @Override
    public String toString() {
        return "BooleanFieldSetter{" +
                "field=" + field +
                '}';
    }
}
