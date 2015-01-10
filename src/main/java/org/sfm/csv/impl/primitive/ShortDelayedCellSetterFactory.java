package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.DelayedCellSetterFactory;
import org.sfm.csv.impl.cellreader.ShortCellValueReader;
import org.sfm.reflect.primitive.ShortSetter;

public class ShortDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Short> {

	private final ShortSetter<T> setter;
	private final ShortCellValueReader reader;

	public ShortDelayedCellSetterFactory(ShortSetter<T> setter, ShortCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public DelayedCellSetter<T, Short> newCellSetter() {
		return new ShortDelayedCellSetter<T>(setter, reader);
	}
}
