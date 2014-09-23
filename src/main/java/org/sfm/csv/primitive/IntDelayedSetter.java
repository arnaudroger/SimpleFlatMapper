package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.csv.cell.IntegerCellValueReader;
import org.sfm.reflect.primitive.IntSetter;

public class IntDelayedSetter<T> implements DelayedCellSetter<T, Integer> {

	private final IntSetter<T> setter;
	private int value;
	
	public IntDelayedSetter(IntSetter<T> setter) {
		this.setter = setter;
	}

	@Override
	public Integer getValue() {
		int v = value;
		value = 0;
		return new Integer(v);
	}

	@Override
	public void set(T t) throws Exception {
		int v = value;
		value = 0;
		setter.setInt(t, v);
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}

	@Override
	public void set(byte[] bytes, int offset, int length) throws Exception {
		this.value = IntegerCellValueReader.parseInt(bytes, offset, length);
	}

}
