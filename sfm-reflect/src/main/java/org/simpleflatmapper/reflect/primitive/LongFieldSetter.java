package org.simpleflatmapper.reflect.primitive;

import java.lang.reflect.Field;

public final class LongFieldSetter<T> implements LongSetter<T> {

	private final Field field;
	
	public LongFieldSetter(final Field field) {
		this.field = field;
	}

	@Override
	public void setLong(final T target, final long value) throws IllegalArgumentException, IllegalAccessException {
		field.setLong(target, value);
	}

    @Override
    public String toString() {
        return "LongFieldSetter{" +
                "field=" + field +
                '}';
    }
}
