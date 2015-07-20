package org.sfm.csv.impl.primitive;

import org.sfm.csv.mapper.BreakDetector;
import org.sfm.csv.mapper.CsvMapperCellConsumer;
import org.sfm.csv.mapper.DelayedCellSetter;
import org.sfm.csv.mapper.DelayedCellSetterFactory;
import org.sfm.csv.impl.cellreader.IntegerCellValueReader;
import org.sfm.reflect.primitive.IntSetter;

public class IntDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Integer> {

	private final IntSetter<T> setter;
	private final IntegerCellValueReader reader;

	public IntDelayedCellSetterFactory(IntSetter<T> setter, IntegerCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public DelayedCellSetter<T, Integer> newCellSetter(BreakDetector breakDetector, CsvMapperCellConsumer<?>[] cellHandlers) {
		return new IntDelayedCellSetter<T>(setter, reader);
	}

    @Override
    public boolean hasSetter() {
        return setter != null;
    }

    @Override
    public String toString() {
        return "IntDelayedCellSetterFactory{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
