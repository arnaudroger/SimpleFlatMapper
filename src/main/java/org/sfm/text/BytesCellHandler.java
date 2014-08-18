package org.sfm.text;

public interface BytesCellHandler {
	void newRow();
	void newCell(byte[] bytes, int offset, int length);
}
