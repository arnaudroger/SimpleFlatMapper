package org.sfm.csv.parser;

public interface CharsCellHandler {
	void newCell(char[] chars, int offset, int length);
	void endOfRow();
	void end();
}
