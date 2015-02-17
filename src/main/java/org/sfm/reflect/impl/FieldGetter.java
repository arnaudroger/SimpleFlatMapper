package org.sfm.reflect.impl;

import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;

import java.lang.reflect.Field;

public final class FieldGetter<T, P> implements Getter<T, P> {

	private final Field field;

    public FieldGetter(final Field field) {
		this.field = field;
	}

	public P get(final T target) throws IllegalAccessException {
		return (P) field.get(target);
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
