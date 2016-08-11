package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.BreakDetector;
import org.simpleflatmapper.csv.mapper.CsvMapperCellConsumer;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.csv.mapper.DelayedCellSetterFactory;
import org.simpleflatmapper.csv.impl.cellreader.FloatCellValueReader;
import org.simpleflatmapper.reflect.primitive.FloatSetter;

public class FloatDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Float> {

	private final FloatSetter<? super T> setter;
	private final FloatCellValueReader reader;

	public FloatDelayedCellSetterFactory(FloatSetter<? super T> setter, FloatCellValueReader reader) {
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
