package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
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
		return ((IntDelayedCellSetter<T>)target[index]).getInt();
	}

	@Override
	public Integer get(DelayedCellSetter<T, ?>[] target) throws Exception {
		return Integer.valueOf(getInt(target));
	}
}
