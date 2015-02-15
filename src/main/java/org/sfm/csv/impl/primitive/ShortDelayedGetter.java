package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.ShortGetter;

public class ShortDelayedGetter<T> implements ShortGetter<DelayedCellSetter<T, ?>[]>, Getter<DelayedCellSetter<T, ?>[], Short> {
	private final int index;
	
	public ShortDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public short getShort(DelayedCellSetter<T, ?>[] target) throws Exception {
		return ((ShortDelayedCellSetter<T>)target[index]).getShort();
	}

	@Override
	public Short get(DelayedCellSetter<T, ?>[] target) throws Exception {
		return Short.valueOf(getShort(target));
	}

    @Override
    public String toString() {
        return "ShortDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
