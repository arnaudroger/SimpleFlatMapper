package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.cellreader.ByteCellValueReader;
import org.simpleflatmapper.reflect.primitive.ByteSetter;

public class ByteCellSetter<T> implements CellSetter<T> {

	private final ByteSetter<? super T> setter;
	private final ByteCellValueReader reader;

	public ByteCellSetter(ByteSetter<? super T> setter, ByteCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext)
			throws Exception {
        if (target == null) return;
        setter.setByte(target, reader.readByte(chars, offset, length, parsingContext));
	}

    @Override
    public String toString() {
        return "ByteCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
