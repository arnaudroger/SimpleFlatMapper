package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.CellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.IntegerCellValueReader;
import org.sfm.reflect.primitive.CharacterSetter;

public class CharCellSetter<T> implements CellSetter<T> {

	private final CharacterSetter<T> setter;
	
	public CharCellSetter(CharacterSetter<T> setter) {
		this.setter = setter;
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext)
			throws Exception {
		setter.setCharacter(target, (char) IntegerCellValueReader.parseInt(chars, offset, length));
	}
}
