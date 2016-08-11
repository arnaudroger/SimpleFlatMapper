package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.BreakDetector;
import org.simpleflatmapper.csv.mapper.CsvMapperCellConsumer;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.csv.mapper.DelayedCellSetterFactory;
import org.simpleflatmapper.csv.impl.cellreader.ByteCellValueReader;
import org.simpleflatmapper.reflect.primitive.ByteSetter;

public class ByteDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Byte> {

	private final ByteSetter<? super T> setter;
	private final ByteCellValueReader reader;

	public ByteDelayedCellSetterFactory(ByteSetter<? super T> setter, ByteCellValueReader reader) {
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
