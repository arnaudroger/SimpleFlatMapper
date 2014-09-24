package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.IntGetter;

@SuppressWarnings("rawtypes")
public class IntDelayedGetter implements IntGetter<DelayedCellSetter[]>, Getter<DelayedCellSetter[], Integer> {
	private final int index;
	
	public IntDelayedGetter(int index) {
		this.index = index;
	}

	@Override
	public int getInt(DelayedCellSetter[] target) throws Exception {
		return ((IntDelayedCellSetter<?>)target[index]).getInt();
	}

	@Override
	public Integer get(DelayedCellSetter[] target) throws Exception {
		return Integer.valueOf(getInt(target));
	}
}
