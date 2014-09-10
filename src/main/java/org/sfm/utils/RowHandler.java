package org.sfm.utils;

public interface RowHandler<T> {
	void handle(T t) throws Exception;
}
