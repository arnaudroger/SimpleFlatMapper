package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.FloatGetter;

@SuppressWarnings("rawtypes")
public class FloatDelayedGetter implements FloatGetter<DelayedCellSetter[]>, Getter<DelayedCellSetter[], Float> {
	private final int index;
	
	public FloatDelayedGetter(int index) {
		this.index = index;
	}

	@Override
	public float getFloat(DelayedCellSetter[] target) throws Exception {
		return ((FloatDelayedCellSetter<?>)target[index]).getFloat();
	}

	@Override
	public Float get(DelayedCellSetter[] target) throws Exception {
		return Float.valueOf(getFloat(target));
	}
}
