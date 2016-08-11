package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;

public class ConstantCharacterGetter<T> implements CharacterGetter<T>, Getter<T, Character> {
    private final char value;

    public ConstantCharacterGetter(char value) {
        this.value = value;
    }

    @Override
    public char getCharacter(T target) {
        return value;
    }

    @Override
    public Character get(T target) {
        return value;
    }
}
