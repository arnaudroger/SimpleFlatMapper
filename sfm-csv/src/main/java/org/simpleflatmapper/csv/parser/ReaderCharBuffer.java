package org.simpleflatmapper.csv.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

public final class ReaderCharBuffer extends CharBuffer {

	private final int maxBufferSize;
	private final Reader reader;
	private int resizeThreshold;

	public ReaderCharBuffer(final int bufferSize, int maxBufferLength, Reader reader) {
		super(new char[bufferSize], 0);
		this.maxBufferSize = maxBufferLength;
		this.reader = reader;
		calculateResizeThreshold();
	}

	public int fillBuffer() throws IOException {
		int length = reader.read(buffer, bufferSize, buffer.length - bufferSize);
		bufferSize += Math.max(0, length);
		return length;
	}

	public int shiftBufferToMark() throws BufferOverflowException {
		// shift buffer consumer data
		int lMark = this.mark;
		int usedLength = Math.max(bufferSize - lMark, 0);

		// if buffer tight double the size
		if (usedLength > resizeThreshold) {
			resize(usedLength);
		}

		System.arraycopy(buffer, lMark, buffer, 0, usedLength);

		bufferSize = usedLength;

		this.mark = 0;
		return lMark;
	}

	private void resize(int requireLength) throws BufferOverflowException {
		int newBufferSize = Math.min(maxBufferSize, buffer.length << 1);

		if (newBufferSize <= requireLength) {
            throw new BufferOverflowException("The content in the csv cell exceed the maxSizeBuffer " + maxBufferSize + ", see CsvParser.DSL.maxSizeBuffer(int) to change the default value");
        }
		// double buffer size
		buffer = Arrays.copyOf(buffer, newBufferSize);

		calculateResizeThreshold();
	}

	private void calculateResizeThreshold() {
		// 3/4 of buffer length
		resizeThreshold = (buffer.length >> 2) * 3;
	}
}
