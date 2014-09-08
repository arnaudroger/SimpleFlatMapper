package org.sfm.reflect.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.sfm.reflect.Setter;

public final class FieldSetter<T, P> implements Setter<T, P> {

	private final Field field;
	private final Type type; 
	
	public FieldSetter(final Field field) {
		this.field = field;
		this.type =  field.getGenericType();
	}

	public void set(final T target, final P value) throws IllegalArgumentException, IllegalAccessException {
		field.set(target, value);
	}

	@Override
	public Type getPropertyType() {
		return type;
	}
	
	public Field getField() {
		return field;
	}
}
