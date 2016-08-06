package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Setter;

import java.lang.reflect.Field;

public final class FieldSetter<T, P> implements Setter<T, P> {

	private final Field field;

    public FieldSetter(final Field field) {
		this.field = field;
	}

	public void set(final T target, final P value) throws IllegalArgumentException, IllegalAccessException {
		field.set(target, value);
	}

	public Field getField() {
		return field;
	}

    @Override
    public String toString() {
        return "FieldSetter{" +
                "field=" + field +
                '}';
    }
}
