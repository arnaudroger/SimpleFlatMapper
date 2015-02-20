package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.LongCellValueReader;
import org.sfm.reflect.primitive.LongSetter;

public class LongDelayedCellSetter<T> implements DelayedCellSetter<T, Long> {

	private final LongSetter<T> setter;
	private final LongCellValueReader reader;
	private long value;

	public LongDelayedCellSetter(LongSetter<T> setter, LongCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public Long getValue() {
		return getLong();
	}
	
	public long getLong() {
		long v = value;
		value = 0;
		return v;
	}

	@Override
	public void set(T t) throws Exception {
		long v = value;
		value = 0;
		setter.setLong(t, v);
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}

	@Override
	public void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception {
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
