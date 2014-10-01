package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.csv.ParsingContext;
import org.sfm.csv.cell.BooleanCellValueReader;
import org.sfm.reflect.primitive.BooleanSetter;

public class BooleanDelayedCellSetter<T> implements DelayedCellSetter<T, Boolean> {

	private final BooleanSetter<T> setter;
	private boolean value;
	
	public BooleanDelayedCellSetter(BooleanSetter<T> setter) {
		this.setter = setter;
	}

	@Override
	public Boolean getValue() {
		return new Boolean(getBoolean());
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
		this.value = BooleanCellValueReader.parseBoolean(chars, offset, length);
	}
}
