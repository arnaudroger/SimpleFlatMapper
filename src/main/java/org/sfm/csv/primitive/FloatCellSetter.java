package org.sfm.csv.primitive;

import org.sfm.csv.CellSetter;
import org.sfm.csv.cell.FloatCellValueReader;
import org.sfm.reflect.primitive.FloatSetter;

public class FloatCellSetter<T> implements CellSetter<T> {

	private final FloatSetter<T> setter;
	
	public FloatCellSetter(FloatSetter<T> setter) {
		this.setter = setter;
	}
	
	@Override
	public void set(T target, byte[] bytes, int offset, int length)
			throws Exception {
		setter.setFloat(target, FloatCellValueReader.parseFloat(bytes, offset, length));
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length)
			throws Exception {
		setter.setFloat(target, FloatCellValueReader.parseFloat(chars, offset, length));
	}
}
