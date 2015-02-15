package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.ByteCellValueReader;
import org.sfm.reflect.primitive.ByteSetter;

public class ByteDelayedCellSetter<T> implements DelayedCellSetter<T, Byte> {

	private final ByteSetter<T> setter;
	private byte value;
	private final ByteCellValueReader reader;

	public ByteDelayedCellSetter(ByteSetter<T> setter, ByteCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
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
		this.value = reader.readByte(chars, offset, length, parsingContext);
	}

    @Override
    public String toString() {
        return "ByteDelayedCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
