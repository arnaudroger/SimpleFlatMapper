package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.cellreader.DoubleCellValueReader;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;

public class DoubleCellSetter<T> implements CellSetter<T> {

	private final DoubleSetter<? super T> setter;
	private final DoubleCellValueReader reader;

	public DoubleCellSetter(DoubleSetter<? super T> setter, DoubleCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}
	
	@Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext)
			throws Exception {
        if (target == null) return;
        setter.setDouble(target, reader.readDouble(chars, offset, length, parsingContext));
	}

    @Override
    public String toString() {
        return "DoubleCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
