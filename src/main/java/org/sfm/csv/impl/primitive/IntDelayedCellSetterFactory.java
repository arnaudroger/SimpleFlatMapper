package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.DelayedCellSetterFactory;
import org.sfm.reflect.primitive.IntSetter;

public class IntDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Integer> {

	private final IntSetter<T> setter;
	
	public IntDelayedCellSetterFactory(IntSetter<T> setter) {
		this.setter = setter;
	}

	@Override
	public DelayedCellSetter<T, Integer> newCellSetter() {
		return new IntDelayedCellSetter<T>(setter);
	}


}
