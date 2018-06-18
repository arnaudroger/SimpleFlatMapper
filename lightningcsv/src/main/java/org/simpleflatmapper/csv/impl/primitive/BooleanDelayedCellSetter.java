package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.cellreader.BooleanCellValueReader;
import org.simpleflatmapper.reflect.primitive.BooleanSetter;

public class BooleanDelayedCellSetter<T> implements DelayedCellSetter<T, Boolean> {

	private final BooleanSetter<? super T> setter;
	private final BooleanCellValueReader reader;
	private boolean value;
    private boolean isNull;

	public BooleanDelayedCellSetter(BooleanSetter<? super T> setter, BooleanCellValueReader reader) {
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
	public void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception {
        isNull = length == 0;
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
