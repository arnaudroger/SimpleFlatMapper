package org.sfm.csv.primitive;

import org.sfm.csv.CellSetter;
import org.sfm.csv.cell.LongCellValueReader;
import org.sfm.reflect.primitive.LongSetter;

public class LongCellSetter<T> implements CellSetter<T> {

	private final LongSetter<T> setter;
	
	public LongCellSetter(LongSetter<T> setter) {
		this.setter = setter;
	}
	
	@Override
	public void set(T target, byte[] bytes, int offset, int length)
			throws Exception {
		setter.setLong(target, LongCellValueReader.parseLong(bytes, offset, length));
	}
	
	
}
