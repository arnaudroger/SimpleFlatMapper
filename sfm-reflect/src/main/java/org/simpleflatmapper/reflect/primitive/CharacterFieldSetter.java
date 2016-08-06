package org.simpleflatmapper.reflect.primitive;

import java.lang.reflect.Field;

public final class CharacterFieldSetter<T> implements CharacterSetter<T> {

	private final Field field;
	
	public CharacterFieldSetter(final Field field) {
		this.field = field;
	}

	@Override
	public void setCharacter(final T target, final char value) throws IllegalArgumentException, IllegalAccessException {
		field.setChar(target, value);
	}

    @Override
    public String toString() {
        return "CharacterFieldSetter{" +
                "field=" + field +
                '}';
    }
}
