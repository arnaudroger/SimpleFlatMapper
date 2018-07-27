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
	public final boolean next() throws IOException {
		// shift buffer consumer data
		int currentSize = this.bufferSize;
		
		int effectiveMark = Math.min(currentSize, mark);
		int newSize = currentSize - effectiveMark;
		
		// shift left over
		char[] lbuffer = this.buffer;
		System.arraycopy(lbuffer, effectiveMark, lbuffer, 0, newSize);
		mark = 0;

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
			
			if (newBufferSize <= newSize) {
				throw new BufferOverflowException("The content in the csv cell exceed the maxSizeBuffer " + maxBufferSize + ", see CsvParser.DSL.maxSizeBuffer(int) to change the default value");
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
