package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.CellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.IntegerCellValueReader;
import org.sfm.reflect.primitive.IntSetter;

public class IntCellSetter<T> implements CellSetter<T> {

	private final IntSetter<T> setter;
	
	public IntCellSetter(IntSetter<T> setter) {
		this.setter = setter;
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext)
			throws Exception {
		setter.setInt(target, IntegerCellValueReader.parseInt(chars, offset, length));
	}	
}
