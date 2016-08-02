package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.core.reflect.Getter;

public class DelayedGetter<T> implements Getter<CsvMapperCellHandler<?>, T> {
	private final int index;
	
	public DelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(CsvMapperCellHandler<?> target) throws Exception {
		return (T) target.getDelayedCellSetter(index).consumeValue();
	}

    @Override
    public String toString() {
        return "DelayedGetter{" +
                "index=" + index +
                '}';
    }
}
