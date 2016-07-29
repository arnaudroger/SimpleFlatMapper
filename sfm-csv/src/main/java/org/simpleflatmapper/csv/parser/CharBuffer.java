package org.simpleflatmapper.csv.parser;

import java.io.IOException;
import java.io.Reader;

public abstract class CharBuffer {

	protected char[] buffer;
	protected int bufferSize;
	protected int mark;

	public CharBuffer(char[] buffer, final int bufferSize) {
		this.buffer = buffer;
		this.bufferSize = bufferSize;
	}
	

	public abstract boolean fillBuffer() throws IOException;
	public abstract int shiftBufferToMark() throws BufferOverflowException;

	public final void mark(int index) {
		this.mark = index;
	}
	public final int getMark() {
		return mark;
	}

	public final char[] getCharBuffer() {
		return buffer;
	}
	public final int getBufferSize() {
		return bufferSize;
	}

}
