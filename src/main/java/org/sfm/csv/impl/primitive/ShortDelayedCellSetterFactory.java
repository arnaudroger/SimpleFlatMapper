package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.DelayedCellSetterFactory;
import org.sfm.reflect.primitive.ShortSetter;

public class ShortDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Short> {

	private final ShortSetter<T> setter;
	
	public ShortDelayedCellSetterFactory(ShortSetter<T> setter) {
		this.setter = setter;
	}

	@Override
	public DelayedCellSetter<T, Short> newCellSetter() {
		return new ShortDelayedCellSetter<T>(setter);
	}
}
