package org.sfm.csv.impl.primitive;

import org.sfm.csv.mapper.BreakDetector;
import org.sfm.csv.mapper.CsvMapperCellConsumer;
import org.sfm.csv.mapper.DelayedCellSetter;
import org.sfm.csv.mapper.DelayedCellSetterFactory;
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
	public DelayedCellSetter<T, Double> newCellSetter(BreakDetector breakDetector, CsvMapperCellConsumer<?>[] cellHandlers) {
		return new DoubleDelayedCellSetter<T>(setter, reader);
	}

    @Override
    public boolean hasSetter() {
        return setter != null;
    }

    @Override
    public String toString() {
        return "DoubleDelayedCellSetterFactory{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
