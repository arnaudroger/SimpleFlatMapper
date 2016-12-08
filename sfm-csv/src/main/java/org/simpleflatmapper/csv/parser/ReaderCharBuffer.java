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
		char[] lbuffer = this.buffer;
		System.arraycopy(lbuffer, effectiveMark, lbuffer, 0, newSize);
		bufferSize = newSize;
		mark = 0;

		if (newSize > resizeThreshold) {
			int newBufferSize = Math.min(maxBufferSize, lbuffer.length << 1);
			if (newBufferSize <= newSize) {
				throw new BufferOverflowException("The content in the csv cell exceed the maxSizeBuffer " + maxBufferSize + ", see CsvParser.DSL.maxSizeBuffer(int) to change the default value");
			}			// double buffer size
			lbuffer = Arrays.copyOf(lbuffer, newBufferSize);
			this.buffer = lbuffer;
			// 3/4
			resizeThreshold = (newBufferSize >> 2) * 3;
		}

		int l = reader.read(lbuffer, newSize, lbuffer.length - newSize);
		bufferSize += Math.max(0, l);
		return l > 0;
	}

}
