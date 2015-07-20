package org.sfm.csv.impl.primitive;

import org.sfm.csv.mapper.BreakDetector;
import org.sfm.csv.mapper.CsvMapperCellConsumer;
import org.sfm.csv.mapper.DelayedCellSetter;
import org.sfm.csv.mapper.DelayedCellSetterFactory;
import org.sfm.csv.impl.cellreader.ByteCellValueReader;
import org.sfm.reflect.primitive.ByteSetter;

public class ByteDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Byte> {

	private final ByteSetter<T> setter;
	private final ByteCellValueReader reader;

	public ByteDelayedCellSetterFactory(ByteSetter<T> setter, ByteCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public DelayedCellSetter<T, Byte> newCellSetter(BreakDetector breakDetector, CsvMapperCellConsumer<?>[] cellHandlers) {
		return new ByteDelayedCellSetter<T>(setter, reader);
	}

    @Override
    public boolean hasSetter() {
        return setter != null;
    }

    @Override
    public String toString() {
        return "ByteDelayedCellSetterFactory{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
