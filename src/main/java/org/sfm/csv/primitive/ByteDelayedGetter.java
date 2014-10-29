package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.ByteGetter;

public class ByteDelayedGetter<T> implements ByteGetter<DelayedCellSetter<T, ?>[]>, Getter<DelayedCellSetter<T, ?>[], Byte> {
	private final int index;
	
	public ByteDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public byte getByte(DelayedCellSetter<T, ?>[] target) throws Exception {
		return ((ByteDelayedCellSetter<T>)target[index]).getByte();
	}

	@Override
	public Byte get(DelayedCellSetter<T, ?>[] target) throws Exception {
		return Byte.valueOf(getByte(target));
	}
}
