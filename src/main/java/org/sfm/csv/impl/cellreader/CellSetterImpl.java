package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.CellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.reflect.Setter;

public final class CellSetterImpl<T, P> implements CellSetter<T> {

	private final CellValueReader<? extends P> reader;
	private final Setter<T, ? super P> setter;

	public CellSetterImpl(CellValueReader<? extends P> reader,
			Setter<T, ? super P> setter) {
		this.reader = reader;
		this.setter = setter;
	}

	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception {
		final P value = reader.read(chars, offset, length, parsingContext);
		setter.set(target, value);
	}

    @Override
    public String toString() {
        return "CellSetterImpl{" +
                "reader=" + reader +
                ", setter=" + setter +
                '}';
    }
}
