package org.sfm.text;

public interface BytesCellHandler {
	void cell(long row, long col, byte[] bytes, int offset, int length);
}
