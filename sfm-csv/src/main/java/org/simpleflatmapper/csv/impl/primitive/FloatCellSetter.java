package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.cellreader.FloatCellValueReader;
import org.simpleflatmapper.core.reflect.primitive.FloatSetter;

public class FloatCellSetter<T> implements CellSetter<T> {

	private final FloatSetter<T> setter;
	private final FloatCellValueReader reader;

	public FloatCellSetter(FloatSetter<T> setter, FloatCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext)
			throws Exception {
        if (target == null) return;
        setter.setFloat(target, reader.readFloat(chars, offset, length, parsingContext));
	}

    @Override
    public String toString() {
        return "FloatCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
