package org.sfm.csv.parser;

public interface BytesCellHandler {
	void endOfRow();
	void newCell(byte[] bytes, int offset, int length);
	void end();
}
