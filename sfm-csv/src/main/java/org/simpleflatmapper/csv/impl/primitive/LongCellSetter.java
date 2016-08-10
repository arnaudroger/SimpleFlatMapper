package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.cellreader.LongCellValueReader;
import org.simpleflatmapper.reflect.primitive.LongSetter;

public class LongCellSetter<T> implements CellSetter<T> {

	private final LongSetter<? super T> setter;
	private final LongCellValueReader reader;

	public LongCellSetter(LongSetter<? super T> setter, LongCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext)
			throws Exception {
        if (target == null) return;
        setter.setLong(target, reader.readLong(chars, offset, length, parsingContext));
	}

    @Override
    public String toString() {
        return "LongCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
