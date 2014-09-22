package org.sfm.csv;

import org.sfm.reflect.Getter;

@SuppressWarnings("rawtypes")
public class DelayedGetter<T> implements Getter<DelayedSetter[], T> {
	private final int index;
	
	public DelayedGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(DelayedSetter[] target) throws Exception {
		return (T) target[index].getValue();
	}

}
