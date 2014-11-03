package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.CellSetter;
import org.sfm.csv.impl.ParsingContext;
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
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception {
		final P value = reader.read(chars, offset, length, parsingContext);
		setter.set(target, value);
	}

}
