package org.sfm.csv;

public interface CellValueReader<T> {
	T read(byte[] bytes, int offset, int length, DecoderContext dc);
	T read(char[] chars, int offset, int length);
}
