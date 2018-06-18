package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;

public class FloatDelayedGetter<T> implements FloatGetter<CsvMapperCellHandler<T>>, Getter<CsvMapperCellHandler<T>, Float> {
	private final int index;
	
	public FloatDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public float getFloat(CsvMapperCellHandler<T> target) throws Exception {
		return ((FloatDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeFloat();
	}

	@Override
	public Float get(CsvMapperCellHandler<T> target) throws Exception {
		return getFloat(target);
	}

    @Override
    public String toString() {
        return "FloatDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
