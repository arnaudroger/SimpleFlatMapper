package org.sfm.reflect.impl;

import java.lang.reflect.Field;

import org.sfm.reflect.Setter;

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
}
