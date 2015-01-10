package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.DelayedCellSetterFactory;
import org.sfm.csv.impl.cellreader.DoubleCellValueReader;
import org.sfm.reflect.primitive.DoubleSetter;

public class DoubleDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Double> {

	private final DoubleSetter<T> setter;
	private final DoubleCellValueReader reader;

	public DoubleDelayedCellSetterFactory(DoubleSetter<T> setter, DoubleCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public DelayedCellSetter<T, Double> newCellSetter() {
		return new DoubleDelayedCellSetter<T>(setter, reader);
	}
}
