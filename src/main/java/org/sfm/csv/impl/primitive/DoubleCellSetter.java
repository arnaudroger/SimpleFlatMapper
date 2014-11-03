package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.CellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.DoubleCellValueReader;
import org.sfm.reflect.primitive.DoubleSetter;

public class DoubleCellSetter<T> implements CellSetter<T> {

	private final DoubleSetter<T> setter;
	
	public DoubleCellSetter(DoubleSetter<T> setter) {
		this.setter = setter;
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext)
			throws Exception {
		setter.setDouble(target, DoubleCellValueReader.parseDouble(chars, offset, length));
	}
}
