package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.BooleanGetter;

public class BooleanDelayedGetter<T> implements BooleanGetter<DelayedCellSetter<T, ?>[]>, Getter<DelayedCellSetter<T, ?>[], Boolean> {
	private final int index;
	
	public BooleanDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean getBoolean(DelayedCellSetter<T, ?>[] target) throws Exception {
		return ((BooleanDelayedCellSetter<T>)target[index]).getBoolean();
	}

	@Override
	public Boolean get(DelayedCellSetter<T, ?>[] target) throws Exception {
		return Boolean.valueOf(getBoolean(target));
	}
}
