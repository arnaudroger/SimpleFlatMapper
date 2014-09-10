package org.sfm.utils;

import java.util.ArrayList;
import java.util.List;

public final class ListHandler<T> implements RowHandler<T> {

	private final List<T> list = new ArrayList<T>();
	
	@Override
	public void handle(final T t) {
		list.add(t);
	}

	public List<T> getList() {
		return list;
	}
}
