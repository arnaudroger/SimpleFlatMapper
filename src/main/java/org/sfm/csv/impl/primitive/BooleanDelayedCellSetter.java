package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.BooleanCellValueReader;
import org.sfm.reflect.primitive.BooleanSetter;

public class BooleanDelayedCellSetter<T> implements DelayedCellSetter<T, Boolean> {

	private final BooleanSetter<T> setter;
	private final BooleanCellValueReader reader;
	private boolean value;
    private boolean isNull;

	public BooleanDelayedCellSetter(BooleanSetter<T> setter, BooleanCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public Boolean consumeValue() {
		return isNull ? null : consumeBoolean();
	}

    @Override
    public Boolean peekValue() {
        return isNull ? null : value;
    }

    public boolean consumeBoolean() {
		boolean v = value;
		value = false;
        isNull = true;
		return v;
	}
	
	@Override
	public void set(T t) throws Exception {
		setter.setBoolean(t, consumeBoolean());
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}

	@Override
	public void set(CharSequence value, ParsingContext parsingContext) throws Exception {
        isNull = value.length() == 0;
		this.value = reader.readBoolean(value, parsingContext);
	}

    @Override
    public String toString() {
        return "BooleanDelayedCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
