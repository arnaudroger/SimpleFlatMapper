package org.sfm.utils;

public final class ArrayCollectorHandler<T> implements RowHandler<T> {

	private final T[] array;
	private int index;

	public ArrayCollectorHandler(T[] array) {
		this.index = 0;
		this.array = array;
	}

	@Override
	public void handle(final T t) {
		array[index]  = t;
		index++;
	}

}
