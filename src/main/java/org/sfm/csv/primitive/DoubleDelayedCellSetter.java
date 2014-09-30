package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.csv.cell.DoubleCellValueReader;
import org.sfm.reflect.primitive.DoubleSetter;

public class DoubleDelayedCellSetter<T> implements DelayedCellSetter<T, Double> {

	private final DoubleSetter<T> setter;
	private double value;
	
	public DoubleDelayedCellSetter(DoubleSetter<T> setter) {
		this.setter = setter;
	}

	@Override
	public Double getValue() {
		return new Double(getDouble());
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
	public void set(char[] chars, int offset, int length) throws Exception {
		this.value = DoubleCellValueReader.parseDouble(chars, offset, length);
	}
}
