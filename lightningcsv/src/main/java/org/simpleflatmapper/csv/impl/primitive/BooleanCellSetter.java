package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.cellreader.BooleanCellValueReader;
import org.simpleflatmapper.reflect.primitive.BooleanSetter;

public class BooleanCellSetter<T> implements CellSetter<T> {

	private final BooleanSetter<? super T> setter;
	private final BooleanCellValueReader reader;
	
	public BooleanCellSetter(BooleanSetter<? super T> setter, BooleanCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext)
			throws Exception {
        if (target == null) return;
        setter.setBoolean(target, reader.readBoolean(chars, offset, length, parsingContext));
	}

    @Override
    public String toString() {
        return "BooleanCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
