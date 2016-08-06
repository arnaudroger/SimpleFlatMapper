package org.simpleflatmapper.csv.impl.primitive;

import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.LongGetter;

public class LongDelayedGetter<T> implements LongGetter<CsvMapperCellHandler<T>>, Getter<CsvMapperCellHandler<T>, Long> {
	private final int index;
	
	public LongDelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public long getLong(CsvMapperCellHandler<T> target) throws Exception {
		return ((LongDelayedCellSetter<T>)target.getDelayedCellSetter(index)).consumeLong();
	}

	@Override
	public Long get(CsvMapperCellHandler<T> target) throws Exception {
		return getLong(target);
	}

    @Override
    public String toString() {
        return "LongDelayedGetter{" +
                "index=" + index +
                '}';
    }
}
