package org.simpleflatmapper.csv.parser;

import java.io.IOException;

public abstract class CharBuffer {

	protected char[] buffer;
	protected int bufferSize;

	public CharBuffer(char[] buffer, final int bufferSize) {
		this.buffer = buffer;
		this.bufferSize = bufferSize;
	}


	public abstract boolean fillBuffer() throws IOException;
	public abstract void shiftBufferToMark(int mark) throws BufferOverflowException;
	public abstract boolean supportsShift();


	public final char[] getCharBuffer() {
		return buffer;
	}
	public final int getBufferSize() {
		return bufferSize;
	}

}
