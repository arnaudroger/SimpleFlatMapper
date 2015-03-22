package org.sfm.csv.impl;

import org.sfm.reflect.Getter;

public class DelayedGetter<T> implements Getter<CsvMapperCellHandlerImpl<?>, T> {
	private final int index;
	
	public DelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(CsvMapperCellHandlerImpl<?> target) throws Exception {
		return (T) target.getDelayedCellSetter(index).consumeValue();
	}

    @Override
    public String toString() {
        return "DelayedGetter{" +
                "index=" + index +
                '}';
    }
}
