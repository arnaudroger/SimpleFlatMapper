package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.cellreader.ByteCellValueReader;
import org.simpleflatmapper.reflect.primitive.ByteSetter;

public class ByteDelayedCellSetter<T> implements DelayedCellSetter<T, Byte> {

	private final ByteSetter<? super T> setter;
	private final ByteCellValueReader reader;
    private byte value;
    private boolean isNull;

	public ByteDelayedCellSetter(ByteSetter<? super T> setter, ByteCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public Byte consumeValue() {
		return isNull ? null : consumeByte();
	}

    @Override
    public Byte peekValue() {
        return isNull ? null : value;
    }

    public byte consumeByte() {
		byte v = value;
		value = 0;
        isNull = true;
		return v;
	}
	
	@Override
	public void set(T t) throws Exception {
		setter.setByte(t, consumeByte());
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}

	@Override
	public void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception {
        isNull = length == 0;
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
