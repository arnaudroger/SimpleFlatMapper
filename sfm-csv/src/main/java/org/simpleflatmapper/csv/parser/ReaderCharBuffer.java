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
		this.resizeThreshold = calculateResizeThreshold();
	}

	@Override
	public final int fillBuffer() throws IOException {
		int length = reader.read(buffer, bufferSize, buffer.length - bufferSize);
		bufferSize += Math.max(0, length);
		return length;
	}

	@Override
	public final int shiftBufferToMark(int mark) throws BufferOverflowException {
		// shift buffer consumer data
		int usedLength = Math.max(bufferSize - mark, 0);

		// if buffer tight double the size
		resizeIfNeeded(usedLength);

		System.arraycopy(buffer, mark, buffer, 0, usedLength);

		bufferSize = usedLength;

		return mark;
	}

	private void resizeIfNeeded(int requireLength) throws BufferOverflowException {
		if (requireLength > resizeThreshold) {
			int newBufferSize = Math.min(maxBufferSize, buffer.length << 1);

			if (newBufferSize <= requireLength) {
				throw new BufferOverflowException("The content in the csv cell exceed the maxSizeBuffer " + maxBufferSize + ", see CsvParser.DSL.maxSizeBuffer(int) to change the default value");
			}
			// double buffer size
			buffer = Arrays.copyOf(buffer, newBufferSize);

			resizeThreshold = calculateResizeThreshold();
		}
	}

	private int calculateResizeThreshold() {
		// 3/4 of buffer length
		return (buffer.length >> 2) * 3;
	}
}
