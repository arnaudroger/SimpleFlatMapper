package org.simpleflatmapper.csv.parser;

import java.io.IOException;

public abstract class CharBuffer {

	protected char[] buffer;
	protected int bufferSize;
	protected int mark;

    public CharBuffer(char[] buffer, final int bufferSize) {
		this.buffer = buffer;
		this.bufferSize = bufferSize;
	}

	public abstract int fillBuffer() throws IOException;
	public abstract int shiftBufferToMark() throws BufferOverflowException;

	public final char[] getCharBuffer() {
		return buffer;
	}
	public final int getBufferSize() {
		return bufferSize;
	}

}
