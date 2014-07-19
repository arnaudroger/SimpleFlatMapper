package org.flatmap.reflect;

import java.lang.reflect.Field;

public class FieldSetter<T, P> implements Setter<T, P> {

	private final Field field;
	
	
	public FieldSetter(Field field) {
		super();
		this.field = field;
	}

	public void set(T target, P value) throws IllegalArgumentException, IllegalAccessException {
		field.set(target, value);
	}
}
