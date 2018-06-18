package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.cellreader.IntegerCellValueReader;
import org.simpleflatmapper.reflect.primitive.IntSetter;

public class IntCellSetter<T> implements CellSetter<T> {

	private final IntSetter<? super T> setter;
	private final IntegerCellValueReader reader;

	public IntCellSetter(IntSetter<? super T> setter, IntegerCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext)
			throws Exception {
        if (target == null) return;
        setter.setInt(target, reader.readInt(chars, offset, length, parsingContext));
	}

    @Override
    public String toString() {
        return "IntCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
