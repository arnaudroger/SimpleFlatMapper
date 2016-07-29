package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.BreakDetector;
import org.simpleflatmapper.csv.mapper.CsvMapperCellConsumer;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.csv.mapper.DelayedCellSetterFactory;
import org.simpleflatmapper.csv.impl.cellreader.IntegerCellValueReader;
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
