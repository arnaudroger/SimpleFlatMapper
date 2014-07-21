package org.sfm.reflect.primitive;

import java.lang.reflect.Field;

public class CharacterFieldSetter<T> implements CharacterSetter<T> {

	private final Field field;
	
	public CharacterFieldSetter(Field field) {
		this.field = field;
	}

	@Override
	public void setCharacter(T target, char value) throws IllegalArgumentException, IllegalAccessException {
		field.setChar(target, value);
	}

}
