package org.sfm.utils;

import java.util.ArrayList;
import java.util.List;

public class ListHandler<T> implements Handler<T> {

	private final List<T> list = new ArrayList<T>();
	
	@Override
	public void handle(T t) throws Exception {
		list.add(t);
	}

	public List<T> getList() {
		return list;
	}
}
