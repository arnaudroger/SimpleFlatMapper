package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.CellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.FloatCellValueReader;
import org.sfm.reflect.primitive.FloatSetter;

public class FloatCellSetter<T> implements CellSetter<T> {

	private final FloatSetter<T> setter;
	
	public FloatCellSetter(FloatSetter<T> setter) {
		this.setter = setter;
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext)
			throws Exception {
		setter.setFloat(target, FloatCellValueReader.parseFloat(chars, offset, length));
	}
}
