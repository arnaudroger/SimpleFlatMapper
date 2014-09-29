package org.sfm.csv.primitive;

import org.sfm.csv.CellSetter;
import org.sfm.csv.DecoderContext;
import org.sfm.csv.cell.BooleanCellValueReader;
import org.sfm.reflect.primitive.BooleanSetter;

public class BooleanCellSetter<T> implements CellSetter<T> {

	private final BooleanSetter<T> setter;
	
	public BooleanCellSetter(BooleanSetter<T> setter) {
		this.setter = setter;
	}
	
	@Override
	public void set(T target, byte[] bytes, int offset, int length, DecoderContext decoderContext)
			throws Exception {
		setter.setBoolean(target, BooleanCellValueReader.parseBoolean(bytes, offset, length));
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length)
			throws Exception {
		setter.setBoolean(target, BooleanCellValueReader.parseBoolean(chars, offset, length));
	}
	
}
