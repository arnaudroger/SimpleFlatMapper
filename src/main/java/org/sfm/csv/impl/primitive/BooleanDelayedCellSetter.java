package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.BooleanCellValueReader;
import org.sfm.reflect.primitive.BooleanSetter;

public class BooleanDelayedCellSetter<T> implements DelayedCellSetter<T, Boolean> {

	private final BooleanSetter<T> setter;
	private final BooleanCellValueReader reader;
	private boolean value;

	public BooleanDelayedCellSetter(BooleanSetter<T> setter, BooleanCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public Boolean getValue() {
		return getBoolean();
	}

	public boolean getBoolean() {
		boolean v = value;
		value = false;
		return v;
	}
	
	@Override
	public void set(T t) throws Exception {
		boolean v = value;
		value = false;
		setter.setBoolean(t, v);
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}

	@Override
	public void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception {
		this.value = reader.readBoolean(chars, offset, length, parsingContext);
	}

    @Override
    public String toString() {
        return "BooleanDelayedCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
