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
	public final boolean next() throws IOException {
		// shift buffer consumer data
		int effectiveMark = Math.min(bufferSize, mark);
		int newSize = bufferSize - effectiveMark;
		System.arraycopy(buffer, effectiveMark, buffer, 0, newSize);
		bufferSize = newSize;
		mark = 0;

		if (bufferSize > resizeThreshold) {
			int newBufferSize = Math.min(maxBufferSize, buffer.length << 1);
			if (newBufferSize <= bufferSize) {
				throw new BufferOverflowException("The content in the csv cell exceed the maxSizeBuffer " + maxBufferSize + ", see CsvParser.DSL.maxSizeBuffer(int) to change the default value");
			}			// double buffer size
			buffer = Arrays.copyOf(buffer, newBufferSize);
			// 3/4
			resizeThreshold = (newBufferSize >> 2) * 3;
		}

		int l = reader.read(buffer, bufferSize, buffer.length - bufferSize);
		bufferSize += Math.max(0, l);
		return l > 0;
	}

}
