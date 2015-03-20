package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.AbstractTargetSetters;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.ByteGetter;

public class ByteDelayedGetter<T> implements ByteGetter<AbstractTargetSetters<T>>, Getter<AbstractTargetSetters<T>, Byte> {
	private final int index;
	
	public ByteDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public byte getByte(AbstractTargetSetters<T> target) throws Exception {
		return ((ByteDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeByte();
	}

	@Override
	public Byte get(AbstractTargetSetters<T> target) throws Exception {
		return getByte(target);
	}

    @Override
    public String toString() {
        return "ByteDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
