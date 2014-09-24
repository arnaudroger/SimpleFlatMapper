package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.BooleanGetter;

@SuppressWarnings("rawtypes")
public class BooleanDelayedGetter implements BooleanGetter<DelayedCellSetter[]>, Getter<DelayedCellSetter[], Boolean> {
	private final int index;
	
	public BooleanDelayedGetter(int index) {
		this.index = index;
	}

	@Override
	public boolean getBoolean(DelayedCellSetter[] target) throws Exception {
		return ((BooleanDelayedCellSetter<?>)target[index]).getBoolean();
	}

	@Override
	public Boolean get(DelayedCellSetter[] target) throws Exception {
		return Boolean.valueOf(getBoolean(target));
	}
}
