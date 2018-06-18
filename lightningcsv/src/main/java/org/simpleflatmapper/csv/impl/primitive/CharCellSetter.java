package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.cellreader.CharCellValueReader;
import org.simpleflatmapper.reflect.primitive.CharacterSetter;

public class CharCellSetter<T> implements CellSetter<T> {

	private final CharacterSetter<? super T> setter;
	private final CharCellValueReader reader;

	public CharCellSetter(CharacterSetter<? super T> setter, CharCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext)
			throws Exception {
        if (target == null) return;
        setter.setCharacter(target, reader.readChar(chars, offset, length, parsingContext));
	}

    @Override
    public String toString() {
        return "CharCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
