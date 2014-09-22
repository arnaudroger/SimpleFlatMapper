package org.sfm.csv.cell;

import org.sfm.csv.CellSetter;
import org.sfm.csv.CellValueReader;
import org.sfm.reflect.Setter;

public final class CellSetterImpl<T, P> implements CellSetter<T> {

	private final CellValueReader<P> reader;
	private final Setter<T, P> setter;

	public CellSetterImpl(CellValueReader<P> reader,
			Setter<T, P> setter) {
		this.reader = reader;
		this.setter = setter;
	}

	@Override
	public void set(T target, byte[] bytes, int offset, int length) throws Exception {
		setter.set(target, reader.read(bytes, offset, length));
	}

}
