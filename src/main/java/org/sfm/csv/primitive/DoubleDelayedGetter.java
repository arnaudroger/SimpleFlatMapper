package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.DoubleGetter;

@SuppressWarnings("rawtypes")
public class DoubleDelayedGetter implements DoubleGetter<DelayedCellSetter[]>, Getter<DelayedCellSetter[], Double> {
	private final int index;
	
	public DoubleDelayedGetter(int index) {
		this.index = index;
	}

	@Override
	public double getDouble(DelayedCellSetter[] target) throws Exception {
		return ((DoubleDelayedCellSetter<?>)target[index]).getDouble();
	}

	@Override
	public Double get(DelayedCellSetter[] target) throws Exception {
		return Double.valueOf(getDouble(target));
	}
}
