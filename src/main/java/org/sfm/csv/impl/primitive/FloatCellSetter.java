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
	public void set(T target, CharSequence value, ParsingContext parsingContext)
			throws Exception {
        if (target == null) return;
        setter.setFloat(target, reader.readFloat(value, parsingContext));
	}

    @Override
    public String toString() {
        return "FloatCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
