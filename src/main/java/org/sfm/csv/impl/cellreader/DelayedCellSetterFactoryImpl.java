package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.BreakDetector;
import org.sfm.csv.impl.CsvMapperCellConsumer;
import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.DelayedCellSetterFactory;
import org.sfm.reflect.Setter;

public class DelayedCellSetterFactoryImpl<T, P> implements DelayedCellSetterFactory<T, P> {

	private final CellValueReader<? extends P> reader;
	private final Setter<T, ? super P> setter;
	
	public DelayedCellSetterFactoryImpl(CellValueReader<? extends P> reader, Setter<T, ? super P> setter) {
		this.reader = reader;
		this.setter = setter;
	}

	@Override
	public DelayedCellSetter<T, P> newCellSetter(BreakDetector breakDetector, CsvMapperCellConsumer<?>[] cellHandlers) {
		return new DelayedCellSetterImpl<T, P>(setter, reader);
	}

    @Override
    public boolean hasSetter() {
        return setter != null;
    }

    @Override
    public String toString() {
        return "DelayedCellSetterFactoryImpl{" +
                "reader=" + reader +
                ", setter=" + setter +
                '}';
    }
}
