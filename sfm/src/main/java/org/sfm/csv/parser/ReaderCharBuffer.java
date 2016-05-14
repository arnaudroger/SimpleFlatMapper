package org.sfm.csv.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

public final class ReaderCharBuffer extends CharBuffer {

	private final int maxBufferSize;
	private final Reader reader;

	public ReaderCharBuffer(final int bufferSize, int maxBufferLength, Reader reader) {
		super(new char[bufferSize], 0);
		this.maxBufferSize = maxBufferLength;
		this.reader = reader;
	}
	

	public boolean fillBuffer() throws IOException {
		int length = reader.read(buffer, bufferSize, buffer.length - bufferSize);
		if (length != -1) {
			bufferSize += length;
			return true;
		} else {
			return false;
		}
	}

	public int shiftBufferToMark() throws BufferOverflowException {
		// shift buffer consumer data
		int usedLength = Math.max(bufferSize - mark, 0);

		// if buffer tight double the size
		if (usedLength > (bufferSize >> 2) * 3) {
			resize(usedLength);
		}

		System.arraycopy(buffer, mark, buffer, 0, usedLength);

		bufferSize = usedLength;

		int m = mark;
		mark = 0;
		return m;
	}

	private void resize(int requireLength) throws BufferOverflowException {
		int newBufferSize = Math.min(maxBufferSize, buffer.length << 1);

		if (newBufferSize <= requireLength) {
            throw new BufferOverflowException("The content in the csv cell exceed the maxSizeBuffer " + maxBufferSize + ", see CsvParser.DSL.maxSizeBuffer(int) to change the default value");
        }
		// double buffer size
		buffer = Arrays.copyOf(buffer, newBufferSize);
	}

	public char[] getCharBuffer() {
		return buffer;
	}

	public int getMark() {
		return mark;
	}

	public char getChar(int bufferIndex) {
		return buffer[bufferIndex];
	}

	public int getBufferSize() {
		return bufferSize;
	}
}
