package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
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
		return ((ByteDelayedCellSetter<T>)target[index]).consumeByte();
	}

	@Override
	public Byte get(DelayedCellSetter<T, ?>[] target) throws Exception {
		return getByte(target);
	}

    @Override
    public String toString() {
        return "ByteDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
