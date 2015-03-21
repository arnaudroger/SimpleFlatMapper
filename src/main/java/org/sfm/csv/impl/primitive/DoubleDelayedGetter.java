package org.sfm.csv.impl.primitive;

import org.sfm.csv.impl.CsvCellHandler;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.DoubleGetter;

public class DoubleDelayedGetter<T> implements DoubleGetter<CsvCellHandler<T>>, Getter<CsvCellHandler<T>, Double> {
	private final int index;
	
	public DoubleDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public double getDouble(CsvCellHandler<T> target) throws Exception {
		return ((DoubleDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeDouble();
	}

	@Override
	public Double get(CsvCellHandler<T> target) throws Exception {
		return getDouble(target);
	}

    @Override
    public String toString() {
        return "DoubleDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
