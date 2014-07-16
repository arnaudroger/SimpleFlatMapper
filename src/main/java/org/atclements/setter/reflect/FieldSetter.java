package org.atclements.setter.reflect;

import java.lang.reflect.Field;

import org.atclements.setter.Setter;

public class FieldSetter implements Setter {

	private final Field field;
	
	
	public FieldSetter(Field field) {
		super();
		this.field = field;
	}


	public void set(Object target, Object value) throws IllegalArgumentException, IllegalAccessException {
		field.set(target, value);
	}

}
