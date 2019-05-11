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
		
		int leftOverSize = currentSize - shiftFrom;
		
		// shift left over
		char[] buffer = this.buffer;
		System.arraycopy(buffer, shiftFrom, buffer, 0, leftOverSize);
		cellStartMark -= shiftFrom;
		rowStartMark -= shiftFrom;

		int availableSpace = buffer.length - leftOverSize;
	
		// make sure we can read readSize
		int readSize = this.readSize;
		if  (availableSpace >= readSize){
			int l = reader.read(buffer, leftOverSize, readSize);

			if (l >= 0) {
				this.bufferSize = leftOverSize + l;
				return true;
			} else {
				this.bufferSize = leftOverSize;
				return false;
			}
		} else {
			return readWithResize(leftOverSize, buffer, readSize);
		}
	}

	private boolean readWithResize(int leftOverSize, char[] buffer, int readSize) throws IOException {
		int newBufferSize = calculateNewBufferSize(leftOverSize);

		buffer = Arrays.copyOf(buffer, newBufferSize);
		this.buffer = buffer;

		int effectiveReadSize = Math.min(readSize, newBufferSize - leftOverSize);

		int l = reader.read(buffer, leftOverSize, effectiveReadSize);

		if (l >= 0) {
			this.bufferSize = leftOverSize + l;
			return true;
		} else {
			this.bufferSize = leftOverSize;
			return false;
		}
	}

	private int calculateNewBufferSize(int leftOverSize) throws BufferOverflowException {
		int newBufferSize = Math.min(buffer.length + Math.max(buffer.length, readSize), maxBufferSize);

		if (leftOverSize >= newBufferSize) {
			throw new BufferOverflowException("The content in the csv cell exceed the maxSizeBuffer " + maxBufferSize + ",  "+ leftOverSize  + ", see CsvParser.DSL.maxSizeBuffer(int) to change the default value");
		}
		return newBufferSize;
	}

}
