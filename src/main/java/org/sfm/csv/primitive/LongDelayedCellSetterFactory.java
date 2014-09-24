package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.csv.DelayedCellSetterFactory;
import org.sfm.reflect.primitive.LongSetter;

public class LongDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Long> {

	private final LongSetter<T> setter;
	
	public LongDelayedCellSetterFactory(LongSetter<T> setter) {
		this.setter = setter;
	}

	@Override
	public DelayedCellSetter<T, Long> newCellSetter() {
		return new LongDelayedCellSetter<T>(setter);
	}


}
