package org.sfm.csv;

public interface CellValueTransfomer<T> {
	T transform(byte[] bytes, int offset, int length);
}
