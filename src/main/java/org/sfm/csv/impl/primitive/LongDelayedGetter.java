package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.LongGetter;

public class LongDelayedGetter<T> implements LongGetter<DelayedCellSetter<T, ?>[]>, Getter<DelayedCellSetter<T, ?>[], Long> {
	private final int index;
	
	public LongDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public long getLong(DelayedCellSetter<T, ?>[] target) throws Exception {
		return ((LongDelayedCellSetter<T>)target[index]).consumeLong();
	}

	@Override
	public Long get(DelayedCellSetter<T, ?>[] target) throws Exception {
		return getLong(target);
	}

    @Override
    public String toString() {
        return "LongDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
