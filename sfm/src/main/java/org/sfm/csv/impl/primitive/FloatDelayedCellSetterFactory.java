package org.sfm.csv.impl.primitive;

import org.sfm.csv.mapper.BreakDetector;
import org.sfm.csv.mapper.CsvMapperCellConsumer;
import org.sfm.csv.mapper.DelayedCellSetter;
import org.sfm.csv.mapper.DelayedCellSetterFactory;
import org.sfm.csv.impl.cellreader.FloatCellValueReader;
import org.sfm.reflect.primitive.FloatSetter;

public class FloatDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Float> {

	private final FloatSetter<T> setter;
	private final FloatCellValueReader reader;

	public FloatDelayedCellSetterFactory(FloatSetter<T> setter, FloatCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public DelayedCellSetter<T, Float> newCellSetter(BreakDetector breakDetector, CsvMapperCellConsumer<?>[] cellHandlers) {
		return new FloatDelayedCellSetter<T>(setter, reader);
	}

    @Override
    public boolean hasSetter() {
        return setter != null;
    }

    @Override
    public String toString() {
        return "FloatDelayedCellSetterFactory{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
