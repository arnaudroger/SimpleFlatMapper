package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.CharCellValueReader;
import org.sfm.reflect.primitive.CharacterSetter;

public class CharDelayedCellSetter<T> implements DelayedCellSetter<T, Character> {

	private final CharacterSetter<T> setter;
	private final CharCellValueReader reader;
    private char value;
    private boolean isNull;

	public CharDelayedCellSetter(CharacterSetter<T> setter, CharCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public Character consumeValue() {
		return isNull ? null : consumeCharacter();
	}

    @Override
    public Character peekValue() {
        return isNull ? null : value;
    }

    public char consumeCharacter() {
		char v = value;
		value = 0;
        isNull = true;
		return v;
	}
	
	@Override
	public void set(T t) throws Exception {
		setter.setCharacter(t, consumeCharacter());
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}

	@Override
	public void set(CharSequence value, ParsingContext parsingContext) throws Exception {
        isNull = value.length() == 0;
		this.value = reader.readChar(value, parsingContext);
	}

    @Override
    public String toString() {
        return "CharDelayedCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
