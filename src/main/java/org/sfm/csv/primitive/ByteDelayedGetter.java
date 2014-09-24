package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.ByteGetter;

@SuppressWarnings("rawtypes")
public class ByteDelayedGetter implements ByteGetter<DelayedCellSetter[]>, Getter<DelayedCellSetter[], Byte> {
	private final int index;
	
	public ByteDelayedGetter(int index) {
		this.index = index;
	}

	@Override
	public byte getByte(DelayedCellSetter[] target) throws Exception {
		return ((ByteDelayedCellSetter<?>)target[index]).getByte();
	}

	@Override
	public Byte get(DelayedCellSetter[] target) throws Exception {
		return Byte.valueOf(getByte(target));
	}
}
