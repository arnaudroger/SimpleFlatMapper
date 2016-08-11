package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.cellreader.CharCellValueReader;
import org.simpleflatmapper.reflect.primitive.CharacterSetter;

public class CharDelayedCellSetter<T> implements DelayedCellSetter<T, Character> {

	private final CharacterSetter<? super T> setter;
	private final CharCellValueReader reader;
    private char value;
    private boolean isNull;

	public CharDelayedCellSetter(CharacterSetter<? super T> setter, CharCellValueReader reader) {
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
	public void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception {
        isNull = length == 0;
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
