package org.sfm.csv;

public interface CellSetter<T> {
	void set(T target, byte[] bytes, int offset, int length) throws Exception;
}
