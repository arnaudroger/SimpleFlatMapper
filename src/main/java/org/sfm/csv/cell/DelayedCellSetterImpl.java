package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.DelayedCellSetter;
import org.sfm.csv.DelayedSetter;
import org.sfm.reflect.Setter;

public class DelayedCellSetterImpl<T, P> implements DelayedCellSetter<T, P> {

	private final CellValueReader<P> reader;
	private final Setter<T, P> setter;
	
	public DelayedCellSetterImpl(CellValueReader<P> reader, Setter<T, P> setter) {
		this.reader = reader;
		this.setter = setter;
	}

	@Override
	public DelayedSetter<T, P> set(byte[] bytes, int offset, int length)
			throws Exception {
		final P value = reader.read(bytes, offset, length);
		return new DelayedSetterImpl<T, P>(value, setter);
	}

}
