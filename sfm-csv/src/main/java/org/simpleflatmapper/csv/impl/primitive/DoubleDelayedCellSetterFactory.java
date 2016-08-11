package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.BreakDetector;
import org.simpleflatmapper.csv.mapper.CsvMapperCellConsumer;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.csv.mapper.DelayedCellSetterFactory;
import org.simpleflatmapper.csv.impl.cellreader.DoubleCellValueReader;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;

public class DoubleDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Double> {

	private final DoubleSetter<? super T> setter;
	private final DoubleCellValueReader reader;

	public DoubleDelayedCellSetterFactory(DoubleSetter<? super T> setter, DoubleCellValueReader reader) {
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
