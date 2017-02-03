package org.simpleflatmapper.reflect.setter;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.CharacterSetter;

public final class IndexedCharArraySetter implements Setter<char[], Character>, CharacterSetter<char[]> {
    private final int index;

    public IndexedCharArraySetter(int index) {
        this.index = index;
    }

    @Override
    public void setCharacter(char[] target, char value) throws Exception {
        target[index] = value;
    }

    @Override
    public void set(char[] target, Character value) throws Exception {
        setCharacter(target, value);
    }
}
