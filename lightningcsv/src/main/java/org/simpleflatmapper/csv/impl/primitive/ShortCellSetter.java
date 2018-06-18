package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.cellreader.ShortCellValueReader;
import org.simpleflatmapper.reflect.primitive.ShortSetter;

public class ShortCellSetter<T> implements CellSetter<T> {

	private final ShortSetter<? super T> setter;
	private final ShortCellValueReader reader;

	public ShortCellSetter(ShortSetter<? super T> setter, ShortCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext)
			throws Exception {
        if (target == null) return;
        setter.setShort(target, reader.readShort(chars, offset, length, parsingContext));
	}

    @Override
    public String toString() {
        return "ShortCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
