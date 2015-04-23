package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.CellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.ByteCellValueReader;
import org.sfm.reflect.primitive.ByteSetter;

public class ByteCellSetter<T> implements CellSetter<T> {

	private final ByteSetter<T> setter;
	private final ByteCellValueReader reader;

	public ByteCellSetter(ByteSetter<T> setter, ByteCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}
	
	@Override
	public void set(T target, CharSequence value, ParsingContext parsingContext)
			throws Exception {
        if (target == null) return;
        setter.setByte(target, reader.readByte(value, parsingContext));
	}

    @Override
    public String toString() {
        return "ByteCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
