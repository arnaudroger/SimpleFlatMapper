package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.csv.DelayedCellSetterFactory;
import org.sfm.reflect.primitive.ByteSetter;

public class ByteDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Byte> {

	private final ByteSetter<T> setter;
	
	public ByteDelayedCellSetterFactory(ByteSetter<T> setter) {
		this.setter = setter;
	}

	@Override
	public DelayedCellSetter<T, Byte> newCellSetter() {
		return new ByteDelayedCellSetter<T>(setter);
	}
}
