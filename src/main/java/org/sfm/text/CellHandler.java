package org.sfm.text;

public interface CellHandler {
	void cell(long row, long col, char[] chars, int offset, int length);
}
