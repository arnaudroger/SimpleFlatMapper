package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.DelayedCellSetterFactory;
import org.sfm.csv.DelayedCellSetter;
import org.sfm.reflect.Setter;

public class DelayedCellSetterFactoryImpl<T, P> implements DelayedCellSetterFactory<T, P> {

	private final CellValueReader<P> reader;
	private final Setter<T, P> setter;
	
	public DelayedCellSetterFactoryImpl(CellValueReader<P> reader, Setter<T, P> setter) {
		this.reader = reader;
		this.setter = setter;
	}

	@Override
	public DelayedCellSetter<T, P> newCellSetter() {
		return new DelayedCellSetterImpl<T, P>(setter, reader);
	}


}
