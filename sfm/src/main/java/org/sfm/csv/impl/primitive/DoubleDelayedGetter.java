package org.sfm.csv.impl.primitive;

import org.sfm.csv.mapper.CsvMapperCellHandler;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.DoubleGetter;

public class DoubleDelayedGetter<T> implements DoubleGetter<CsvMapperCellHandler<T>>, Getter<CsvMapperCellHandler<T>, Double> {
	private final int index;
	
	public DoubleDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public double getDouble(CsvMapperCellHandler<T> target) throws Exception {
		return ((DoubleDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeDouble();
	}

	@Override
	public Double get(CsvMapperCellHandler<T> target) throws Exception {
		return getDouble(target);
	}

    @Override
    public String toString() {
        return "DoubleDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
