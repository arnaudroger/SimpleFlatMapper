package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.IntegerCellValueReader;
import org.sfm.reflect.primitive.IntSetter;

public class IntDelayedCellSetter<T> implements DelayedCellSetter<T, Integer> {

	private final IntSetter<T> setter;
	private final IntegerCellValueReader reader;
	private int value;

	public IntDelayedCellSetter(IntSetter<T> setter, IntegerCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public Integer getValue() {
		return new Integer(getInt());
	}

	public int getInt() {
		int v = value;
		value = 0;
		return v;
	}
	
	@Override
	public void set(T t) throws Exception {
		int v = value;
		value = 0;
		setter.setInt(t, v);
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}

	@Override
	public void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception {
		this.value = reader.readInt(chars, offset, length, parsingContext);
	}

    @Override
    public String toString() {
        return "IntDelayedCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
