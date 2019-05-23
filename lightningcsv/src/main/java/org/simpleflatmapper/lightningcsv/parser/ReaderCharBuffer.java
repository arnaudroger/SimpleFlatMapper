package org.simpleflatmapper.lightningcsv.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

public final class ReaderCharBuffer extends CharBuffer {

	private final int maxBufferSize;
	private final Reader reader;
	private final int readSize;

	public ReaderCharBuffer(final int readSize, int maxBufferLength, Reader reader) {
		super(new char[readSize < 4096 ? readSize : readSize * 2], 0);
		this.readSize = readSize;
		this.maxBufferSize = maxBufferLength;
		this.reader = reader;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public final boolean shiftAndRead(int shiftFrom) throws IOException {
		// shift buffer consumer data
		int currentSize = this.bufferSize;
		
		int newSize = currentSize - shiftFrom;
		
		// shift left over
		char[] lbuffer = this.buffer;
		System.arraycopy(lbuffer, shiftFrom, lbuffer, 0, newSize);
		cellStartMark -= shiftFrom;
		rowStartMark -= shiftFrom;

		int bufferLength = lbuffer.length;

		int availableSpace = bufferLength - newSize;

		int effectiveReadSize = readSize;
	
		// make sure we can read readSize
		if (availableSpace < effectiveReadSize) {
			int newBufferSize = bufferLength * 2;

			if (newBufferSize < bufferLength + effectiveReadSize) {
				newBufferSize =  bufferLength + effectiveReadSize;
			}

			if (newBufferSize > maxBufferSize) {
				newBufferSize = maxBufferSize;
			}

			if (newSize >= newBufferSize) {
				throw new BufferOverflowException("The content in the csv cell exceed the maxSizeBuffer " + maxBufferSize + ",  "+ newSize  + ", see CsvParser.DSL.maxSizeBuffer(int) to change the default value");
			}


			lbuffer = Arrays.copyOf(lbuffer, newBufferSize);
			this.buffer = lbuffer;

			if (effectiveReadSize > bufferLength - newSize) {
				effectiveReadSize = bufferLength - newSize;
			}
		}

		int l = reader.read(lbuffer, newSize, effectiveReadSize);

			if (l >= 0) {
			this.bufferSize = newSize + l;
			return true;
		} else {
			this.bufferSize = newSize;
			return false;
		}
	}

}
