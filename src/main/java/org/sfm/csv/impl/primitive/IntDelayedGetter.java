package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.IntGetter;

public class IntDelayedGetter<T> implements IntGetter<DelayedCellSetter<T, ?>[]>, Getter<DelayedCellSetter<T, ?>[], Integer> {
	private final int index;
	
	public IntDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getInt(DelayedCellSetter<T, ?>[] target) throws Exception {
		return ((IntDelayedCellSetter<T>)target[index]).consumeInt();
	}

	@Override
	public Integer get(DelayedCellSetter<T, ?>[] target) throws Exception {
		return getInt(target);
	}

    @Override
    public String toString() {
        return "IntDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
