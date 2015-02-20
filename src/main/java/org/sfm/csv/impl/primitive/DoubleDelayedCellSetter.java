package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.DoubleCellValueReader;
import org.sfm.reflect.primitive.DoubleSetter;

public class DoubleDelayedCellSetter<T> implements DelayedCellSetter<T, Double> {

	private final DoubleSetter<T> setter;
	private final DoubleCellValueReader reader;
	private double value;

	public DoubleDelayedCellSetter(DoubleSetter<T> setter, DoubleCellValueReader reader) {
		this.setter = setter;
		this.reader = reader;
	}

	@Override
	public Double getValue() {
		return getDouble();
	}

	public double getDouble() {
		double v = value;
		value = 0;
		return v;
	}
	
	@Override
	public void set(T t) throws Exception {
		double v = value;
		value = 0;
		setter.setDouble(t, v);
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}

	@Override
	public void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception {
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
