package org.sfm.text;

public interface CharsCellHandler {
	void cell(long row, long col, char[] chars, int offset, int length);
}
