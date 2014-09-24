package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.ShortGetter;

@SuppressWarnings("rawtypes")
public class ShortDelayedGetter implements ShortGetter<DelayedCellSetter[]>, Getter<DelayedCellSetter[], Short> {
	private final int index;
	
	public ShortDelayedGetter(int index) {
		this.index = index;
	}

	@Override
	public short getShort(DelayedCellSetter[] target) throws Exception {
		return ((ShortDelayedCellSetter<?>)target[index]).getShort();
	}

	@Override
	public Short get(DelayedCellSetter[] target) throws Exception {
		return Short.valueOf(getShort(target));
	}
}
