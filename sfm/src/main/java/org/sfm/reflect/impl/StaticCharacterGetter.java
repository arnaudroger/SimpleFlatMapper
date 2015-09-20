package org.sfm.reflect.impl;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.CharacterGetter;

public class StaticCharacterGetter<T> implements CharacterGetter, Getter<T, Character> {
    private final char value;

    public StaticCharacterGetter(char value) {
        this.value = value;
    }

    @Override
    public char getCharacter(Object target) throws Exception {
        return value;
    }

    @Override
    public Character get(T target) throws Exception {
        return value;
    }
}
