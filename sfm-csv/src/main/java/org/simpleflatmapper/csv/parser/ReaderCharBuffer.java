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
		this.resizeThreshold = (buffer.length >> 2) * 3;
	}

	@Override
	public final int fillBuffer() throws IOException {
		resizeIfNeeded();
		return readToBuffer();
	}

	private void resizeIfNeeded() throws BufferOverflowException {
		int usedLength = usedLength();
		if (usedLength > resizeThreshold) {
			int newBufferSize = Math.min(maxBufferSize, buffer.length << 1);

			if (newBufferSize <= usedLength) {
				throw new BufferOverflowException("The content in the csv cell exceed the maxSizeBuffer " + maxBufferSize + ", see CsvParser.DSL.maxSizeBuffer(int) to change the default value");
			}
			// double buffer size
			buffer = Arrays.copyOf(buffer, newBufferSize);

			// 3/4
			resizeThreshold = (buffer.length >> 2) * 3;
		}
	}

	private int readToBuffer() throws IOException {
		int l = reader.read(buffer, bufferSize, buffer.length - bufferSize);
		if (l > 0) {
			bufferSize += l;
		}
		return l;
	}
}
