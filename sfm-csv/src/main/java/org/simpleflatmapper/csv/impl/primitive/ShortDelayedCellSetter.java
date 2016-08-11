package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.cellreader.ShortCellValueReader;
import org.simpleflatmapper.reflect.primitive.ShortSetter;

public class ShortDelayedCellSetter<T> implements DelayedCellSetter<T, Short> {

	private final ShortSetter<? super T> setter;
	private final ShortCellValueReader reader;
	private short value;
    private boolean isNull;

	public ShortDelayedCellSetter(ShortSetter<? super T> setter, ShortCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public Short consumeValue() {
        return isNull ? null : getShort();
	}

    public Short peekValue() {
        return isNull ? null : value;
    }

	public short getShort() {
		short v = value;
		value = 0;
        isNull = true;
		return v;
	}
	
	@Override
	public void set(T t) throws Exception {
		setter.setShort(t, consumeValue());
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}

	@Override
	public void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception {
        isNull = length ==0;
        this.value = reader.readShort(chars, offset, length, parsingContext);
	}

    @Override
    public String toString() {
        return "ShortDelayedCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
