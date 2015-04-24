package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.LongCellValueReader;
import org.sfm.reflect.primitive.LongSetter;

public class LongDelayedCellSetter<T> implements DelayedCellSetter<T, Long> {

	private final LongSetter<T> setter;
	private final LongCellValueReader reader;
	private long value;
    private boolean isNull;

	public LongDelayedCellSetter(LongSetter<T> setter, LongCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public Long consumeValue() {
		return isNull ? null : consumeLong();
	}

    @Override
    public Long peekValue() {
        return isNull ? null : value;
    }

    public long consumeLong() {
		long v = value;
		value = 0;
        isNull = true;
		return v;
	}

	@Override
	public void set(T t) throws Exception {
		setter.setLong(t, consumeLong());
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}

	@Override
	public void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception {
        isNull = length == 0;
		this.value = reader.readLong(chars, offset, length, parsingContext);
	}

    @Override
    public String toString() {
        return "LongDelayedCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
