package org.sfm.csv.impl.primitive;

import org.sfm.csv.CsvMapper;
import org.sfm.csv.impl.BreakDetector;
import org.sfm.csv.impl.CsvMapperCellConsumer;
import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.DelayedCellSetterFactory;
import org.sfm.csv.impl.cellreader.BooleanCellValueReader;
import org.sfm.reflect.primitive.BooleanSetter;

import java.util.Map;

public class BooleanDelayedCellSetterFactory<T> implements DelayedCellSetterFactory<T, Boolean> {

	private final BooleanSetter<T> setter;
	private final BooleanCellValueReader reader;

	public BooleanDelayedCellSetterFactory(BooleanSetter<T> setter, BooleanCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public DelayedCellSetter<T, Boolean> newCellSetter(BreakDetector breakDectector, CsvMapperCellConsumer<?>[]  cellHandlers) {
		return new BooleanDelayedCellSetter<T>(setter, reader);
	}

    @Override
    public String toString() {
        return "BooleanDelayedCellSetterFactory{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
