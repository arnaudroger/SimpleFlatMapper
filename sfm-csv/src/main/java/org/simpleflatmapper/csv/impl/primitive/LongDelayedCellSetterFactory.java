package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.BreakDetector;
import org.simpleflatmapper.csv.mapper.CsvMapperCellConsumer;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.csv.mapper.DelayedCellSetterFactory;
import org.simpleflatmapper.csv.impl.cellreader.LongCellValueReader;
import org.simpleflatmapper.reflect.primitive.LongSetter;

public class LongDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Long> {

	private final LongSetter<? super T> setter;
	private final LongCellValueReader reader;

	public LongDelayedCellSetterFactory(LongSetter<? super T> setter, LongCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public DelayedCellSetter<T, Long> newCellSetter(BreakDetector breakDetector, CsvMapperCellConsumer<?>[] cellHandlers) {
		return new LongDelayedCellSetter<T>(setter, reader);
	}

    @Override
    public boolean hasSetter() {
        return setter != null;
    }

    @Override
    public String toString() {
        return "LongDelayedCellSetterFactory{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
