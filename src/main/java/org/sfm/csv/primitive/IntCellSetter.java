package org.sfm.csv.primitive;

import org.sfm.csv.CellSetter;
import org.sfm.csv.DecoderContext;
import org.sfm.csv.cell.IntegerCellValueReader;
import org.sfm.reflect.primitive.IntSetter;

public class IntCellSetter<T> implements CellSetter<T> {

	private final IntSetter<T> setter;
	
	public IntCellSetter(IntSetter<T> setter) {
		this.setter = setter;
	}
	
	@Override
	public void set(T target, byte[] bytes, int offset, int length, DecoderContext dc)
			throws Exception {
		setter.setInt(target, IntegerCellValueReader.parseInt(bytes, offset, length));
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length)
			throws Exception {
		setter.setInt(target, IntegerCellValueReader.parseInt(chars, offset, length));
	}	
}
