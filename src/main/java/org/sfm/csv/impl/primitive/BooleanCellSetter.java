package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.CellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.BooleanCellValueReader;
import org.sfm.reflect.primitive.BooleanSetter;

public class BooleanCellSetter<T> implements CellSetter<T> {

	private final BooleanSetter<T> setter;
	
	public BooleanCellSetter(BooleanSetter<T> setter) {
		this.setter = setter;
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext)
			throws Exception {
		setter.setBoolean(target, BooleanCellValueReader.parseBoolean(chars, offset, length));
	}
	
}
