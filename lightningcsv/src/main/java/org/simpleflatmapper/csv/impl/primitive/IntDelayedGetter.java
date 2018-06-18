package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.IntGetter;

public class IntDelayedGetter<T> implements IntGetter<CsvMapperCellHandler<T>>, Getter<CsvMapperCellHandler<T>, Integer> {
	private final int index;
	
	public IntDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getInt(CsvMapperCellHandler<T> target) throws Exception {
		return ((IntDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeInt();
	}

	@Override
	public Integer get(CsvMapperCellHandler<T> target) throws Exception {
		return getInt(target);
	}

    @Override
    public String toString() {
        return "IntDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
