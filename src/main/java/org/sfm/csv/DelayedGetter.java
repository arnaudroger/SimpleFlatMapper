package org.sfm.csv;

import org.sfm.reflect.Getter;

@SuppressWarnings("rawtypes")
public class DelayedGetter<T> implements Getter<DelayedCellSetter[], T> {
	private final int index;
	
	public DelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(DelayedCellSetter[] target) throws Exception {
		return (T) target[index].getValue();
	}

}
