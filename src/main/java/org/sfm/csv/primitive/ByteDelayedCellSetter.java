package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.csv.ParsingContext;
import org.sfm.csv.cell.IntegerCellValueReader;
import org.sfm.reflect.primitive.ByteSetter;

public class ByteDelayedCellSetter<T> implements DelayedCellSetter<T, Byte> {

	private final ByteSetter<T> setter;
	private byte value;
	
	public ByteDelayedCellSetter(ByteSetter<T> setter) {
		this.setter = setter;
	}

	@Override
	public Byte getValue() {
		return new Byte(getByte());
	}

	public byte getByte() {
		byte v = value;
		value = 0;
		return v;
	}
	
	@Override
	public void set(T t) throws Exception {
		byte v = value;
		value = 0;
		setter.setByte(t, v);
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}

	@Override
	public void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception {
		this.value = (byte)IntegerCellValueReader.parseInt(chars, offset, length);
	}
}
