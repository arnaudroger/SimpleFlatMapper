package org.sfm.text;

public interface CharsCellHandler {
	void newCell(char[] chars, int offset, int length);
	void newRow();
}
