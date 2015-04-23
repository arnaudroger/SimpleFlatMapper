package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.CellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.CharCellValueReader;
import org.sfm.reflect.primitive.CharacterSetter;

public class CharCellSetter<T> implements CellSetter<T> {

	private final CharacterSetter<T> setter;
	private final CharCellValueReader reader;

	public CharCellSetter(CharacterSetter<T> setter, CharCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}
	
	@Override
	public void set(T target, CharSequence value, ParsingContext parsingContext)
			throws Exception {
        if (target == null) return;
        setter.setCharacter(target, reader.readChar(value, parsingContext));
	}

    @Override
    public String toString() {
        return "CharCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
