package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.BreakDetector;
import org.sfm.csv.impl.CsvMapperCellConsumer;
import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.DelayedCellSetterFactory;
import org.sfm.csv.impl.cellreader.LongCellValueReader;
import org.sfm.reflect.primitive.LongSetter;

public class LongDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Long> {

	private final LongSetter<T> setter;
	private final LongCellValueReader reader;

	public LongDelayedCellSetterFactory(LongSetter<T> setter, LongCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public DelayedCellSetter<T, Long> newCellSetter(BreakDetector breakDectector, CsvMapperCellConsumer<?>[] cellHandlers) {
		return new LongDelayedCellSetter<T>(setter, reader);
	}

    @Override
    public String toString() {
        return "LongDelayedCellSetterFactory{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
