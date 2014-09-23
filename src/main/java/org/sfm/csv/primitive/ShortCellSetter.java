package org.sfm.csv.primitive;

import org.sfm.csv.CellSetter;
import org.sfm.csv.cell.IntegerCellValueReader;
import org.sfm.reflect.primitive.ShortSetter;

public class ShortCellSetter<T> implements CellSetter<T> {

	private final ShortSetter<T> setter;
	
	public ShortCellSetter(ShortSetter<T> setter) {
		this.setter = setter;
	}
	
	@Override
	public void set(T target, byte[] bytes, int offset, int length)
			throws Exception {
		setter.setShort(target, (short) IntegerCellValueReader.parseInt(bytes, offset, length));
	}
}
