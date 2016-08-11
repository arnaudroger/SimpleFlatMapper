package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.BreakDetector;
import org.simpleflatmapper.csv.mapper.CsvMapperCellConsumer;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.csv.mapper.DelayedCellSetterFactory;
import org.simpleflatmapper.csv.impl.cellreader.BooleanCellValueReader;
import org.simpleflatmapper.reflect.primitive.BooleanSetter;

public class BooleanDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Boolean> {

	private final BooleanSetter<? super T> setter;
	private final BooleanCellValueReader reader;

	public BooleanDelayedCellSetterFactory(BooleanSetter<? super T> setter, BooleanCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public DelayedCellSetter<T, Boolean> newCellSetter(BreakDetector breakDetector, CsvMapperCellConsumer<?>[]  cellHandlers) {
		return new BooleanDelayedCellSetter<T>(setter, reader);
	}

    @Override
    public boolean hasSetter() {
        return setter != null;
    }

    @Override
    public String toString() {
        return "BooleanDelayedCellSetterFactory{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
