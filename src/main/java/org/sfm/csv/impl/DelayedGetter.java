package org.sfm.csv.impl;

import org.sfm.reflect.Getter;

public class DelayedGetter<T> implements Getter<DelayedCellSetter<T, ?>[], T> {
	private final int index;
	
	public DelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(DelayedCellSetter<T, ?>[] target) throws Exception {
		return (T) target[index].getValue();
	}

}
