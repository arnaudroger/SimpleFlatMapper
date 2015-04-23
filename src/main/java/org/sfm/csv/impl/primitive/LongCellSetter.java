package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.CellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.LongCellValueReader;
import org.sfm.reflect.primitive.LongSetter;

public class LongCellSetter<T> implements CellSetter<T> {

	private final LongSetter<T> setter;
	private final LongCellValueReader reader;

	public LongCellSetter(LongSetter<T> setter, LongCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}
	
	@Override
	public void set(T target, CharSequence value, ParsingContext parsingContext)
			throws Exception {
        if (target == null) return;
        setter.setLong(target, reader.readLong(value, parsingContext));
	}

    @Override
    public String toString() {
        return "LongCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
