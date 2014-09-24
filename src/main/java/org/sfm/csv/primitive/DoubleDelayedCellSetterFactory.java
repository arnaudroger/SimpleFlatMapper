package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.csv.DelayedCellSetterFactory;
import org.sfm.reflect.primitive.DoubleSetter;

public class DoubleDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Double> {

	private final DoubleSetter<T> setter;
	
	public DoubleDelayedCellSetterFactory(DoubleSetter<T> setter) {
		this.setter = setter;
	}

	@Override
	public DelayedCellSetter<T, Double> newCellSetter() {
		return new DoubleDelayedCellSetter<T>(setter);
	}
}
