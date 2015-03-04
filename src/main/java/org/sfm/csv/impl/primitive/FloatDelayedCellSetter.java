package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.FloatCellValueReader;
import org.sfm.reflect.primitive.FloatSetter;

public class FloatDelayedCellSetter<T> implements DelayedCellSetter<T, Float> {

	private final FloatSetter<T> setter;
	private final FloatCellValueReader reader;
	private float value;
    private boolean isNull;

	public FloatDelayedCellSetter(FloatSetter<T> setter, FloatCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public Float consumeValue() {
		return isNull ? null : consumeFloat();
	}

    @Override
    public Float peekValue() {
        return isNull ? null : value;
    }

    public float consumeFloat() {
		float v = value;
		value = 0;
        isNull = true;
		return v;
	}
	
	@Override
	public void set(T t) throws Exception {
		setter.setFloat(t, consumeValue());
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}

	@Override
	public void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception {
        isNull = length == 0;
		this.value = reader.readFloat(chars, offset, length, parsingContext);
	}

    @Override
    public String toString() {
        return "FloatDelayedCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
