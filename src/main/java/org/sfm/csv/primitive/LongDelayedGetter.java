package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.LongGetter;

@SuppressWarnings("rawtypes")
public class LongDelayedGetter implements LongGetter<DelayedCellSetter[]>, Getter<DelayedCellSetter[], Long> {
	private final int index;
	
	public LongDelayedGetter(int index) {
		this.index = index;
	}

	@Override
	public long getLong(DelayedCellSetter[] target) throws Exception {
		return ((LongDelayedCellSetter<?>)target[index]).getLong();
	}

	@Override
	public Long get(DelayedCellSetter[] target) throws Exception {
		return Long.valueOf(getLong(target));
	}
}
