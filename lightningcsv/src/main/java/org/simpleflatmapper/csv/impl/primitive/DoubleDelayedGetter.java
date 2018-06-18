package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;

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
