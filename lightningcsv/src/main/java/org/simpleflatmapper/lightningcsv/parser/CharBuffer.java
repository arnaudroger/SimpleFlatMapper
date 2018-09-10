package org.simpleflatmapper.lightningcsv.parser;

import java.io.IOException;

public abstract class CharBuffer {

	public char[] buffer;
	public int bufferSize;
	public int cellStartMark;
	public int rowStartMark;

    public CharBuffer(char[] buffer, final int bufferSize) {
		this.buffer = buffer;
		this.bufferSize = bufferSize;
	}

	public abstract boolean isConstant();
	public abstract boolean shiftAndRead(int shiftFrom) throws IOException;
}
