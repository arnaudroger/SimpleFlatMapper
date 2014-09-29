package org.sfm.csv.primitive;

import org.sfm.csv.CellSetter;
import org.sfm.csv.DecoderContext;
import org.sfm.csv.cell.IntegerCellValueReader;
import org.sfm.reflect.primitive.ByteSetter;

public class ByteCellSetter<T> implements CellSetter<T> {

	private final ByteSetter<T> setter;
	
	public ByteCellSetter(ByteSetter<T> setter) {
		this.setter = setter;
	}
	
	@Override
	public void set(T target, byte[] bytes, int offset, int length, DecoderContext dc)
			throws Exception {
		setter.setByte(target, (byte) IntegerCellValueReader.parseInt(bytes, offset, length));
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length)
			throws Exception {
		setter.setByte(target, (byte) IntegerCellValueReader.parseInt(chars, offset, length));
	}
}
