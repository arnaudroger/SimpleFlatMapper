package org.sfm.csv.parser;

public interface CharsCellHandler {
	void newCell(char[] chars, int offset, int length);
	boolean endOfRow();
	void end();
}
