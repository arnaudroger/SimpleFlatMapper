package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.csv.DelayedCellSetterFactory;
import org.sfm.reflect.primitive.FloatSetter;

public class FloatDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Float> {

	private final FloatSetter<T> setter;
	
	public FloatDelayedCellSetterFactory(FloatSetter<T> setter) {
		this.setter = setter;
	}

	@Override
	public DelayedCellSetter<T, Float> newCellSetter() {
		return new FloatDelayedCellSetter<T>(setter);
	}
}
