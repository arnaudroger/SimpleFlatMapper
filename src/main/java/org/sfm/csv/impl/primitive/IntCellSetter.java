package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.CellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.IntegerCellValueReader;
import org.sfm.reflect.primitive.IntSetter;

public class IntCellSetter<T> implements CellSetter<T> {

	private final IntSetter<T> setter;
	private final IntegerCellValueReader reader;

	public IntCellSetter(IntSetter<T> setter, IntegerCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}
	
	@Override
	public void set(T target, CharSequence value, ParsingContext parsingContext)
			throws Exception {
        if (target == null) return;
        setter.setInt(target, reader.readInt(value, parsingContext));
	}

    @Override
    public String toString() {
        return "IntCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
