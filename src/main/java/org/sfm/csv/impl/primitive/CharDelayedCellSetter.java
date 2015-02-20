package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.CharCellValueReader;
import org.sfm.reflect.primitive.CharacterSetter;

public class CharDelayedCellSetter<T> implements DelayedCellSetter<T, Character> {

	private final CharacterSetter<T> setter;
	private char value;
	private final CharCellValueReader reader;

	public CharDelayedCellSetter(CharacterSetter<T> setter, CharCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public Character getValue() {
		return getCharacter();
	}

	public char getCharacter() {
		char v = value;
		value = 0;
		return v;
	}
	
	@Override
	public void set(T t) throws Exception {
		char v = value;
		value = 0;
		setter.setCharacter(t, v);
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}

	@Override
	public void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception {
		this.value = reader.readChar(chars, offset, length, parsingContext);
	}

    @Override
    public String toString() {
        return "CharDelayedCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
