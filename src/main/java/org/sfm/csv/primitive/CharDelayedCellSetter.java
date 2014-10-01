package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.csv.ParsingContext;
import org.sfm.csv.cell.IntegerCellValueReader;
import org.sfm.reflect.primitive.CharacterSetter;

public class CharDelayedCellSetter<T> implements DelayedCellSetter<T, Character> {

	private final CharacterSetter<T> setter;
	private char value;
	
	public CharDelayedCellSetter(CharacterSetter<T> setter) {
		this.setter = setter;
	}

	@Override
	public Character getValue() {
		return new Character(getCharacter());
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
		this.value = (char)IntegerCellValueReader.parseInt(chars, offset, length);
	}
}
