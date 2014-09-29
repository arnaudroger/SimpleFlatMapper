package org.sfm.csv.primitive;

import org.sfm.csv.CellSetter;
import org.sfm.csv.DecoderContext;
import org.sfm.csv.cell.DoubleCellValueReader;
import org.sfm.reflect.primitive.DoubleSetter;

public class DoubleCellSetter<T> implements CellSetter<T> {

	private final DoubleSetter<T> setter;
	private final DoubleCellValueReader reader = new DoubleCellValueReader();
	
	public DoubleCellSetter(DoubleSetter<T> setter) {
		this.setter = setter;
	}
	
	@Override
	public void set(T target, byte[] bytes, int offset, int length, DecoderContext dc)
			throws Exception {
		setter.setDouble(target, reader.parseDouble(bytes, offset, length, dc));
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length)
			throws Exception {
		setter.setDouble(target, DoubleCellValueReader.parseDouble(chars, offset, length));
	}
}
