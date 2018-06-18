package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.cellreader.DoubleCellValueReader;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;

public class DoubleDelayedCellSetter<T> implements DelayedCellSetter<T, Double> {

	private final DoubleSetter<? super T> setter;
	private final DoubleCellValueReader reader;
	private double value;
    private boolean isNull;

	public DoubleDelayedCellSetter(DoubleSetter<? super T> setter, DoubleCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public Double consumeValue() {
		return isNull ? null : consumeDouble();
	}

    @Override
    public Double peekValue() {
        return isNull ? null : value;
    }

    public double consumeDouble() {
		double v = value;
		value = 0;
        isNull = true;
		return v;
	}
	
	@Override
	public void set(T t) throws Exception {
		setter.setDouble(t, consumeValue());
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}

	@Override
	public void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception {
        isNull = length == 0;
		this.value = reader.readDouble(chars, offset, length, parsingContext);
	}

    @Override
    public String toString() {
        return "DoubleDelayedCellSetter{" +
                "setter=" + setter +
                ", reader=" + reader +
                '}';
    }
}
