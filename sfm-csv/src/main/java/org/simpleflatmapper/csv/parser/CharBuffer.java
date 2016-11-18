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

	public final void shiftBufferToMark() throws BufferOverflowException {
		// shift buffer consumer data
		int usedLength = usedLength();
		if (usedLength > 0) {
			System.arraycopy(buffer, mark, buffer, 0, usedLength);
		}
		bufferSize = usedLength;
		mark = 0;
	}

	protected final int usedLength() {
		return Math.max(bufferSize - mark, 0);
	}


}
