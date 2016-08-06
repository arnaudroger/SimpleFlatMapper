package org.simpleflatmapper.reflect.primitive;

import java.lang.reflect.Field;

public final class ShortFieldSetter<T> implements ShortSetter<T> {

	private final Field field;
	
	public ShortFieldSetter(final Field field) {
		this.field = field;
	}

	@Override
	public void setShort(final T target, final short value) throws IllegalArgumentException, IllegalAccessException {
		field.setShort(target, value);
	}

    @Override
    public String toString() {
        return "ShortFieldSetter{" +
                "field=" + field +
                '}';
    }
}
