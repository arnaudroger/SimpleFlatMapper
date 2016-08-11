package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.reflect.Getter;

public class DelayedGetter<T, P> implements Getter<CsvMapperCellHandler<T>, P> {
	private final int index;
	
	public DelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public P get(CsvMapperCellHandler<T> target) throws Exception {
		return (P) target.getDelayedCellSetter(index).consumeValue();
	}

    @Override
    public String toString() {
        return "DelayedGetter{" +
                "index=" + index +
                '}';
    }
}
