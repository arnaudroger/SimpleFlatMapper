package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;

public final class IndexedCharArrayGetter implements Getter<char[], Character>, CharacterGetter<char[]> {
    private final int index;

    public IndexedCharArrayGetter(int index) {
        this.index = index;
    }

    @Override
    public char getCharacter(char[] target) throws Exception {
        return target[index];
    }

    @Override
    public Character get(char[] target) throws Exception {
        return getCharacter(target);
    }
}
