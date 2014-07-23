package org.sfm.reflect;

import java.lang.reflect.Field;

public final class FieldSetter<T, P> implements Setter<T, P> {

	private final Field field;
	private final Class<? extends P> type; 
	
	
	@SuppressWarnings("unchecked")
	public FieldSetter(Field field) {
		this.field = field;
		this.type = (Class<? extends P>) field.getType();
	}

	public void set(T target, P value) throws IllegalArgumentException, IllegalAccessException {
		field.set(target, value);
	}

	@Override
	public Class<? extends P> getPropertyType() {
		return type;
	}
	
	public Field getField() {
		return field;
	}
}
