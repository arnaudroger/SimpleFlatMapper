package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.CellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.FloatCellValueReader;
import org.sfm.reflect.primitive.FloatSetter;

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
